package pe.getsemani.mikhipu.password.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.password.dto.ForgotPasswordRequest;
import pe.getsemani.mikhipu.password.dto.ResetPasswordRequest;
import pe.getsemani.mikhipu.password.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones relacionadas con recuperación de contraseñas")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "Solicitar restablecimiento de contraseña",
            description = "Genera un token de restablecimiento de contraseña y envía un enlace al correo proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo enviado correctamente si el usuario existe.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No se encontró un usuario con el correo proporcionado.",
                    content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok("Si existe una cuenta con ese correo electrónico, se enviará un enlace para restablecer la contraseña.");
    }

    @Operation(summary = "Restablecer contraseña",
            description = "Valida un token activo y permite establecer una nueva contraseña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado.",
                    content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("La contraseña se ha restablecido correctamente.");
    }
}
