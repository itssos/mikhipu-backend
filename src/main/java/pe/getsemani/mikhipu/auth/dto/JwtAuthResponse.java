package pe.getsemani.mikhipu.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {

    @Schema(description = "Token JWT de autenticación", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String token;

    @Schema(description = "Tipo de token de autenticación (generalmente 'Bearer')", example = "Bearer")
    private String tokenType;

    @Schema(description = "Información de la persona autenticada")
    private PersonResponseDTO person;
}
