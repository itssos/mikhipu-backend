package pe.getsemani.mikhipu.password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Schema(description = "Correo electrónico del usuario para recuperar la contraseña", example = "usuario@dominio.com", required = true)
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Debe proporcionar un correo electrónico válido.")
    @Size(max = 100, message = "El correo electrónico debe tener como máximo 100 caracteres.")
    private String email;
}
