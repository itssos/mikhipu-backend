package pe.getsemani.mikhipu.password.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Schema(description = "Token único de recuperación enviado al correo electrónico", example = "4f0d3c16-8d6f-42e3-a5c5-654fe3213abc", required = true)
    @NotBlank(message = "El token de recuperación no puede estar vacío.")
    private String token;

    @Schema(description = "Nueva contraseña a establecer", example = "Nuev0P@ssword", required = true)
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres.")
    private String newPassword;
}
