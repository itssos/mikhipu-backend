
package pe.getsemani.mikhipu.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.model.entity.Role;
import pe.getsemani.mikhipu.model.entity.User;
import pe.getsemani.mikhipu.repository.RoleRepository;
import pe.getsemani.mikhipu.repository.UserRepository;

import java.util.Arrays;
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
        // 1. Leer credenciales de props/env
        String adminUsername = env.getProperty("admin.user.username");
        String adminEmail    = env.getProperty("admin.user.email");
        String adminRawPwd   = env.getProperty("admin.user.password");

        // 2. Asegurar roles base
        List<Role> requiredRoles = Arrays.asList(
                new Role(null, "ROLE_ADMIN",  "Administrator role"),
                new Role(null, "ROLE_USER",   "Default user role")
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

        // 3. Crear admin si no existe
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() ->
                            new IllegalStateException("ROLE_ADMIN should have been created")
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