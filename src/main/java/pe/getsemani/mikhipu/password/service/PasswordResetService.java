package pe.getsemani.mikhipu.password.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.email.service.EmailService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.password.entity.PasswordResetToken;
import pe.getsemani.mikhipu.password.repository.PasswordResetTokenRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private static final int EXPIRATION_HOURS = 1;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Generates a password reset token, saves it, and sends an email with the reset link.
     */
    @Transactional
    public void createPasswordResetToken(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(EXPIRATION_HOURS);

            PasswordResetToken prt = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(expiry)
                    .build();
            tokenRepository.save(prt);

            String resetLink = frontendBaseUrl + "auth/reset-password?token=" + token;
            String htmlBody = buildResetPasswordEmailBody(resetLink);

            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Restablezca su contraseña",
                    htmlBody
            );
        });
    }

    /**
     * Validates the token and updates the user's password.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password reset token"));

        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password reset token has expired");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(prt);
    }

    private String buildResetPasswordEmailBody(String resetLink) {
        return String.format(
                """
                <!DOCTYPE html>
                <html lang="en"><head><meta charset="UTF-8"><title>Reset Password</title></head>
                <body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:0;margin:0">
                  <table align="center" width="100%%" style="max-width:600px;background:#fff;border-radius:8px;overflow:hidden">
                    <tr><td style="background:#4CAF50;padding:20px;text-align:center;color:#fff">
                      <h1 style="margin:0;font-size:24px">Reset Your Password</h1>
                    </td></tr>
                    <tr><td style="padding:20px;color:#333">
                      <p>Hola,</p>
                      <p>Has solicitado restablecer tu contraseña. Haz clic en el botón de abajo:</p>
                      <p style="text-align:center;margin:30px 0">
                        <a href="%s" style="background:#4CAF50;color:#fff;text-decoration:none;padding:12px 24px;border-radius:4px;display:inline-block;font-size:16px">
                          Restablecer contraseña
                        </a>
                      </p>
                      <p>Este enlace caducará en 1 hora. Si no lo solicitaste, ignora este correo electrónico.</p>
                    </td></tr>
                    <tr><td style="background:#f9f9f9;padding:15px;font-size:12px;color:#777">
                      <p>Si el botón no funciona, copie y pegue esta URL:</p>
                      <p><a href="%s" style="color:#4CAF50;word-break:break-all">%s</a></p>
                    </td></tr>
                  </table>
                </body>
                </html>
                """,
                resetLink, resetLink, resetLink
        );
    }
}
