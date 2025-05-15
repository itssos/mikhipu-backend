package pe.getsemani.mikhipu.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {

    @Schema(description = "ID del usuario", example = "10")
    private Integer id;

    @Schema(description = "Nombre de usuario", example = "usuario123")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
    private String email;

    @Schema(description = "Rol asignado al usuario", example = "DOCENTE")
    private String role;

    @Schema(description = "Permisos del usuario asignados a través de su rol", example = "[\"CREATE_TEACHER\", \"GET_TEACHERS\"]")
    private Set<String> permissions;
}
