package pe.getsemani.mikhipu.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private String tokenType;
    private PersonResponseDTO person;
}