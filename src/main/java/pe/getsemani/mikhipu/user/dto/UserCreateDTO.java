package pe.getsemani.mikhipu.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCreateDTO {

    @Schema(description = "Nombre de usuario único", example = "docente123", required = true)
    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(max = 50, message = "El nombre de usuario debe tener como máximo 50 caracteres.")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com", required = true)
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El correo electrónico debe tener un formato válido.")
    @Size(max = 100, message = "El correo electrónico debe tener como máximo 100 caracteres.")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "Segura123!", required = true)
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres.")
    private String password;

    @Schema(description = "Nombre del rol asignado al usuario", example = "DOCENTE", required = true)
    @NotBlank(message = "El rol no puede estar vacío.")
    private String role;
}
