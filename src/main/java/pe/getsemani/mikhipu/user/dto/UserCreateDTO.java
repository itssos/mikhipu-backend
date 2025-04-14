package pe.getsemani.mikhipu.user.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
