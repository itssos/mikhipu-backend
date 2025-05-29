package pe.getsemani.mikhipu.persons.person.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.persons.person.enums.Gender;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;

import java.time.LocalDate;

@Data
public class PersonResponseDTO {

    @Schema(description = "ID único de la persona", example = "15")
    private Long id;

    @Schema(description = "Nombres de la persona", example = "Carlos")
    private String firstName;

    @Schema(description = "Apellidos de la persona", example = "Pérez Gómez")
    private String lastName;

    @Schema(description = "Número de DNI", example = "12345678")
    private String dni;

    @Schema(description = "Género", example = "MASCULINO")
    private Gender gender;

    @Schema(description = "Dirección de residencia", example = "Av. Siempre Viva 742")
    private String address;

    @Schema(description = "Número de teléfono", example = "987654321")
    private String phone;

    @Schema(description = "Fecha de nacimiento", example = "2000-05-15")
    private LocalDate birthDate;

    @Schema(description = "Usuario asociado a la persona")
    private UserResponseDTO user;
}