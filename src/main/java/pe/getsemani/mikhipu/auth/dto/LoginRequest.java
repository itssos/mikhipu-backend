package pe.getsemani.mikhipu.auth.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "Nombre de usuario o correo electrónico del usuario", example = "admin", required = true)
    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "1234", required = true)
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;
}
