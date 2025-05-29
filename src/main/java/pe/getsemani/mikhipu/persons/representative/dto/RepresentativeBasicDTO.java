package pe.getsemani.mikhipu.persons.representative.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RepresentativeBasicDTO {

    @Schema(description = "ID del apoderado")
    private Long id;

    @Schema(description = "Nombre completo del apoderado")
    private String fullName;
}