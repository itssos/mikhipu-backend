package pe.getsemani.mikhipu.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;
import pe.getsemani.mikhipu.assistance.repository.AssistanceSessionRepository;
import pe.getsemani.mikhipu.persons.admin.Admin;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.person.enums.Gender;
import pe.getsemani.mikhipu.persons.admin.AdminRepository;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.PermissionConstants;
import pe.getsemani.mikhipu.role.enums.RoleConstants;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final Environment env;
    private final RoleRepository    roleRepo;
    private final PermissionRepository permRepo;
    private final UserRepository    userRepo;
    private final AdminRepository adminRepository;
    private final PasswordEncoder   passwordEncoder;
    private final PersonRepository personRepository;
    private final AssistanceSessionRepository assistanceSessionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeRoles();
        initializePermissions();
        assignPermissionsToRoles();
        initializeAdminUserAndPerson();
        initAssistanceSession();
    }

    private void initializeRoles() {
        for (RoleConstants rc : RoleConstants.values()) {
            roleRepo.findByName(rc.getCode()).ifPresentOrElse(
                    existing -> log.debug("Rol ya existe: {}", rc.getCode()),
                    () -> {
                        Role r = Role.builder()
                                .name(rc.getCode())
                                .description(rc.getLabel())
                                .build();
                        roleRepo.save(r);
                        log.info("✔ Rol creado: {} ({})", rc.getCode(), rc.getLabel());
                    }
            );
        }
    }

    private void initializePermissions() {
        for (PermissionConstants pc : PermissionConstants.values()) {
            permRepo.findByName(pc.getCode()).ifPresentOrElse(
                    existing -> log.debug("Permiso ya existe: {}", pc.getCode()),
                    () -> {
                        Permission p = Permission.builder()
                                .name(pc.getCode())
                                .label(pc.getLabel())
                                .build();
                        permRepo.save(p);
                        log.info("✔ Permiso creado: {} ({})", pc.getCode(), pc.getLabel());
                    }
            );
        }
    }

    private void assignPermissionsToRoles() {
        Map<RoleConstants,List<PermissionConstants>> mapping = rolePermissionsMapping();
        for (var entry : mapping.entrySet()) {
            RoleConstants rc = entry.getKey();
            Role role = roleRepo.findByName(rc.getCode())
                    .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + rc.getCode()));

            Set<Permission> perms = entry.getValue().stream()
                    .map(pc -> permRepo.findByName(pc.getCode())
                            .orElseThrow(() -> new IllegalStateException("Permiso no encontrado: " + pc.getCode())))
                    .collect(Collectors.toCollection(HashSet::new));

            role.setPermissions(perms);
            roleRepo.save(role);
            log.info("✔ Permisos asignados a {}: {}", rc.getCode(),
                    perms.stream().map(Permission::getName).toList());
        }
    }

    private Map<RoleConstants,List<PermissionConstants>> rolePermissionsMapping() {
        List<PermissionConstants> all = Arrays.asList(PermissionConstants.values());
        return Map.of(
                RoleConstants.ADMINISTRADOR, all,
                RoleConstants.DOCENTE,       List.of(
                        PermissionConstants.GET_PERSONS,
                        PermissionConstants.GET_PERSON,
                        PermissionConstants.GET_ROLES,
                        PermissionConstants.GET_ROLE,
                        PermissionConstants.GET_STUDENTS,
                        PermissionConstants.GET_STUDENT,
                        PermissionConstants.GET_COURSE,
                        PermissionConstants.GET_COURSES,

                        PermissionConstants.GET_EVALUATION,
                        PermissionConstants.CREATE_EVALUATION,
                        PermissionConstants.UPDATE_EVALUATION,
                        PermissionConstants.DELETE_EVALUATION,

                        PermissionConstants.SCORE_REGISTER,
                        PermissionConstants.SCORE_HISTORY_VIEW,
                        PermissionConstants.GET_TEACHERS
                ),
                RoleConstants.ESTUDIANTE,    List.of(
                        PermissionConstants.GET_PERSONS,
                        PermissionConstants.SCORE_AVERAGE_VIEW,
                        PermissionConstants.SCORE_SELF_VIEW,
                        PermissionConstants.GET_STUDENTS,
                        PermissionConstants.GET_STUDENT,
                        PermissionConstants.GET_COURSE,
                        PermissionConstants.GET_COURSES,
                        PermissionConstants.GET_EVALUATION,
                        PermissionConstants.GET_TEACHERS
                ),
                RoleConstants.APODERADO,     List.of(
                        PermissionConstants.GET_PERSONS,
                        PermissionConstants.SCORE_AVERAGE_VIEW,
                        PermissionConstants.SCORE_SELF_VIEW,
                        PermissionConstants.GET_STUDENTS,
                        PermissionConstants.GET_STUDENT,
                        PermissionConstants.GET_COURSE,
                        PermissionConstants.GET_COURSES,
                        PermissionConstants.GET_TEACHERS,
                        PermissionConstants.GET_EVALUATION,
                        PermissionConstants.SCORE_CHILDREN_VIEW
                )
        );
    }

    private void initializeAdminUserAndPerson() {
        String adminUsername = env.getProperty("admin.user.username");
        if (adminUsername == null) {
            log.warn("Propiedad admin.user.username no configurada, omito creación de admin.");
            return;
        }
        if (userRepo.findByUsername(adminUsername).isEmpty()) {
            // Crear User de administrador
            String email = env.getProperty("admin.user.email", "");
            String rawPwd = env.getProperty("admin.user.password", "");
            Role adminRole = roleRepo.findByName(RoleConstants.ADMINISTRADOR.getCode())
                    .orElseThrow(() -> new IllegalStateException("Rol ADMINISTRADOR no encontrado"));
            User adminUser = User.builder()
                    .username(adminUsername)
                    .email(email)
                    .password(passwordEncoder.encode(rawPwd))
                    .active(true)
                    .role(adminRole)
                    .build();
            userRepo.save(adminUser);
            log.info("✔ Usuario administrador creado: {}", adminUsername);

            // Crear y guardar primero la persona
            Person person = new Person();
            person.setFirstName("Sair");
            person.setLastName("Marquez Hidalgo");
            person.setDni("12345678");
            person.setBirthDate(LocalDate.of(2003, 7, 22));
            person.setGender(Gender.MASCULINO);
            person.setAddress("Calle Aleatoria 123");
            person.setPhone("987654321");
            person.setUser(adminUser);

            Person savedPerson = personRepository.save(person);

            // Asociar la persona al admin y guardar
            Admin admin = new Admin();
            admin.setPerson(savedPerson);
            adminRepository.save(admin);
            log.info("✔ Persona creada para admin: {} {}", savedPerson.getFirstName(), savedPerson.getLastName());
        }
    }

    private void initAssistanceSession() {
        boolean exists = assistanceSessionRepository.existsById(1L);

        if (!exists) {
            AssistanceSession assistanceSession1 = AssistanceSession.builder()
                    .startEntryTime(LocalTime.parse("08:00"))
                    .endEntryTime(LocalTime.parse("08:30"))
                    .startExitTime(LocalTime.parse("13:00"))
                    .endExitTime(LocalTime.parse("13:30"))
                    .attendanceDeadline(LocalDateTime.parse("2025-12-30T23:00:00"))
                    .active(true)
                    .build();

            assistanceSessionRepository.save(assistanceSession1);
        } else {
            System.out.println("La sesión con ID 1 ya existe, no se creará una nueva.");
        }
    }
}
