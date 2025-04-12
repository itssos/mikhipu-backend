package pe.getsemani.mikhipu.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas de JwtTokenProvider")
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
    @DisplayName("Generar y validar token correctamente")
    void shouldGenerateAndValidateToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        String token = provider.generateToken(auth);
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsername(token)).isEqualTo("user");
    }

    @Test
    @DisplayName("Token aleatorio no válido retorna false")
    void shouldInvalidateRandomToken() {
        boolean isValid;
        try {
            isValid = provider.validateToken("invalid");
        } catch (JwtException ex) {
            // Se lanza una excepción porque el token no tiene formato correcto, por lo que consideramos que es inválido.
            isValid = false;
        }
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Token expirado retorna false")
    void shouldInvalidateExpiredToken() throws Exception {
        // Se establece la validez en negativo para forzar que el token ya esté vencido
        Field validityField = JwtTokenProvider.class.getDeclaredField("validityInMilliseconds");
        validityField.setAccessible(true);
        validityField.set(provider, -1000L);
        provider.init();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        String token = provider.generateToken(auth);

        boolean isValid;
        try {
            isValid = provider.validateToken(token);
        } catch (JwtException ex) {
            // Se lanza una excepción al validar un token expirado, por lo que se considera inválido.
            isValid = false;
        }
        assertThat(isValid).isFalse();
    }
}
