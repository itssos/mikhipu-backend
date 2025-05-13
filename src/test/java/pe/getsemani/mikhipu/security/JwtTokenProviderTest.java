package pe.getsemani.mikhipu.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {JwtTokenProviderTest.Config.class})
@TestPropertySource(properties = {
        "jwt.secret=claveSuperSecreta12345678901234567890",
        "jwt.expiration-ms=3600000"
})
class JwtTokenProviderTest {

    @TestConfiguration
    @Import(JwtTokenProvider.class)
    static class Config {
        // Inyecta solo lo necesario
    }

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Debe generar y validar token correctamente")
    void generarYValidarToken() {
        var auth = new UsernamePasswordAuthenticationToken(
                "usuario1", "clave", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertEquals("usuario1", jwtTokenProvider.getUsername(token));
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Debe rechazar token inválido")
    void tokenInvalidoLanzaExcepcion() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.falsafirma";
        assertThrows(Exception.class, () -> jwtTokenProvider.validateToken(tokenInvalido));
    }
}
