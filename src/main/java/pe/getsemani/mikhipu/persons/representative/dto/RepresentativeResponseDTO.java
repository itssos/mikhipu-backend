package pe.getsemani.mikhipu.persons.representative.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.persons.representative.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;

@Data
public class RepresentativeResponseDTO {

    @Schema(description = "ID del apoderado")
    private Long id;

    @Schema(description = "Datos personales del apoderado")
    private PersonResponseDTO person;

    @Schema(description = "Tipo de relación con el estudiante")
    private RelationshipType relationship;
}