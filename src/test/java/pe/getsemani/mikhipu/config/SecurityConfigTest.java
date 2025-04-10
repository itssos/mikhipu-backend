package pe.getsemani.mikhipu.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;


class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig(null, null);

    @Test
    void shouldProvideBCryptPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
