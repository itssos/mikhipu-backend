package pe.getsemani.mikhipu.persons.representative.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.persons.representative.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;

import java.util.Set;

@Data
public class RepresentativeCreateDTO {

    @Schema(description = "Datos personales del apoderado", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La persona es obligatoria")
    @Valid
    private PersonCreateDTO person;

    @Schema(description = "Tipo de relación con el estudiante", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El parentesco es obligatorio")
    private RelationshipType relationship;

    @Schema(description = "IDs de estudiantes relacionados")
    @NotEmpty(message = "Debe asignar al menos un estudiante")
    private Set<Long> studentIds;
}