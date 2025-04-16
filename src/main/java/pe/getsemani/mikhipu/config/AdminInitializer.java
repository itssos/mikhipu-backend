package pe.getsemani.mikhipu.config;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.PermissionConstants;
import pe.getsemani.mikhipu.role.enums.RoleConstants;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

@Component
public class AdminInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final Environment env;
    private final RoleRepository    roleRepo;
    private final PermissionRepository permRepo;
    private final UserRepository    userRepo;
    private final PersonRepository  personRepo;
    private final PasswordEncoder   passwordEncoder;

    public AdminInitializer(Environment env,
                            RoleRepository roleRepo,
                            PermissionRepository permRepo,
                            UserRepository userRepo,
                            PersonRepository personRepo,
                            PasswordEncoder passwordEncoder) {
        this.env             = env;
        this.roleRepo        = roleRepo;
        this.permRepo        = permRepo;
        this.userRepo        = userRepo;
        this.personRepo      = personRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeRoles();
        initializePermissions();
        assignPermissionsToRoles();
        initializeAdminUserAndPerson();
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
                        PermissionConstants.UPDATE_PERSON,
                        PermissionConstants.GET_ROLES,
                        PermissionConstants.GET_ROLE
                ),
                RoleConstants.ESTUDIANTE,    List.of(
                        PermissionConstants.GET_PERSONS
                ),
                RoleConstants.APODERADO,     List.of(
                        PermissionConstants.GET_PERSONS
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
                    .roles(Set.of(adminRole))
                    .build();
            userRepo.save(adminUser);
            log.info("✔ Usuario administrador creado: {}", adminUsername);

            // Asignar también una Person ligada a ese User
            Student person = new Student();
            person.setFirstName("Sair");
            person.setLastName("Marquez Hidalgo");
            person.setDni("12345678");
            person.setBirthDate(LocalDate.of(2003, 7, 22));
            person.setGender(Gender.MASCULINO);
            person.setAddress("Calle Aleatoria 123");
            person.setPhone("987654321");
            person.setGrade(6);
            person.setSection(Section.A);
            person.setSchoolLevel(SchoolLevel.PRIMARIA);
            person.setUser(adminUser);
            personRepo.save(person);
            log.info("✔ Persona creada para admin: {} {}", person.getFirstName(), person.getLastName());
        }
    }
}
