package pe.getsemani.mikhipu.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.role.enums.RoleType;

@Data
public class RoleAssignmentDTO {
    @NotNull(message = "Role type must be provided")
    private RoleType roleType;
}
