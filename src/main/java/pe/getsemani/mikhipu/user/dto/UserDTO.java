package pe.getsemani.mikhipu.user.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private Set<String> roles;
}
