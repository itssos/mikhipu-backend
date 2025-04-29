package pe.getsemani.mikhipu.user.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private Set<String> permissions;
}