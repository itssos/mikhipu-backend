
package pe.getsemani.mikhipu.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final Environment env;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(Environment env,
                            RoleRepository roleRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.env = env;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        String adminUsername = env.getProperty("admin.user.username");
        String adminEmail    = env.getProperty("admin.user.email");
        String adminRawPwd   = env.getProperty("admin.user.password");

        List<Role> requiredRoles = List.of(
                new Role(null, RoleType.ADMINISTRADOR, "Rol de Administrador")
        );
        for (Role r : requiredRoles) {
            roleRepository.findByName(r.getName())
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(r.getName())
                                    .description(r.getDescription())
                                    .build()
                    ));
        }

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleType.ADMINISTRADOR)
                    .orElseThrow(() ->
                            new IllegalStateException(RoleType.ADMINISTRADOR+" should have been created")
                    );

            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminRawPwd))
                    .active(true)
                    .roles(Collections.singleton(adminRole))
                    .build();

            userRepository.save(admin);
            System.out.println("✔ Admin user created: " + adminUsername);
        }
    }
}