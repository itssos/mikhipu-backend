package pe.getsemani.mikhipu.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProvider();
        Field secretField = JwtTokenProvider.class.getDeclaredField("secretKeyString");
        secretField.setAccessible(true);
        secretField.set(provider, "01234567890123456789012345678901");
        Field validityField = JwtTokenProvider.class.getDeclaredField("validityInMilliseconds");
        validityField.setAccessible(true);
        validityField.set(provider, 3600000L);
        provider.init();
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        String token = provider.generateToken(auth);
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsername(token)).isEqualTo("user");
    }

    @Test
    void shouldInvalidateRandomToken() {
        assertThat(provider.validateToken("invalid")).isFalse();
    }

    @Test
    void shouldInvalidateExpiredToken() throws Exception {
        Field validityField = JwtTokenProvider.class.getDeclaredField("validityInMilliseconds");
        validityField.setAccessible(true);
        validityField.set(provider, -1000L);
        provider.init();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        String token = provider.generateToken(auth);
        assertThat(provider.validateToken(token)).isFalse();
    }
}
