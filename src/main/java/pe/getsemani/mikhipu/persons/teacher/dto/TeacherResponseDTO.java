package pe.getsemani.mikhipu.persons.teacher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;

@Data
public class TeacherResponseDTO {

    @Schema(description = "ID único del docente", example = "5")
    private Long id;

    @Schema(description = "Código del docente", example = "PROFE-2024")
    private String code;

    @Schema(description = "Datos personales del docente")
    private PersonResponseDTO person;
}
