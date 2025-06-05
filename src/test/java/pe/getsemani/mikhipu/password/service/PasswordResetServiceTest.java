package pe.getsemani.mikhipu.password.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;
import pe.getsemani.mikhipu.email.service.EmailService;
import pe.getsemani.mikhipu.password.entity.PasswordResetToken;
import pe.getsemani.mikhipu.password.repository.PasswordResetTokenRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de restablecimiento de contraseña")
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService service;

    @BeforeEach
    void init() {
        // Fijar la URL base para el frontend
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://app.test");
    }

    @Test
    @DisplayName("Debe resetear la contraseña con token válido")
    void resetPassword_validToken_updatesPasswordAndDeletesToken() {
        User user = new User();
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");
        PasswordResetToken prt = PasswordResetToken.builder()
                .token("tok")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        when(tokenRepository.findByToken("tok")).thenReturn(Optional.of(prt));

        service.resetPassword("tok", "newPass");

        assertThat(user.getPassword()).isEqualTo("encoded");
        verify(userRepository).save(user);
        verify(tokenRepository).delete(prt);
    }

    @Test
    @DisplayName("Debe fallar si el token es inválido")
    void resetPassword_invalidToken_throws() {
        when(tokenRepository.findByToken("bad")).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(
                () -> service.resetPassword("bad", "pw"),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).isEqualTo("El token de restablecimiento es inválido.");
    }

    @Test
    @DisplayName("Debe fallar si el token está expirado")
    void resetPassword_expiredToken_throws() {
        PasswordResetToken prt = PasswordResetToken.builder()
                .token("tok")
                .user(new User())
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        when(tokenRepository.findByToken("tok")).thenReturn(Optional.of(prt));

        ResponseStatusException ex = catchThrowableOfType(
                () -> service.resetPassword("tok", "pw"),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).isEqualTo("El token de restablecimiento ha expirado.");
    }
}
