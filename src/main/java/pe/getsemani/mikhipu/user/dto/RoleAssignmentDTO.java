package pe.getsemani.mikhipu.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleAssignmentDTO {
    @NotEmpty(message = "El rol no debe estar vacio")
    @NotNull(message = "El rol no debe ser nulo")
    private String role;
}
