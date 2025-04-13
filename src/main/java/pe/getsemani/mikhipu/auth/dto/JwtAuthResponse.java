package pe.getsemani.mikhipu.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.getsemani.mikhipu.person.dto.PersonDTO;
import pe.getsemani.mikhipu.user.dto.UserDTO;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private String tokenType;
    private PersonDTO person;
    private UserDTO user;
}