package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssistanceRecordCreateDTO {

    @Schema(description = "ID del estudiante (leído desde el QR)", example = "123", required = true)
    @NotNull
    private Long studentId;

    @Schema(description = "ID de la sesión del día", example = "10", required = true)
    @NotNull
    private Long sessionId;

    @Schema(description = "Tipo de marca (entrada o salida)", example = "entrada", required = true, allowableValues = {"entrada", "salida"})
    @NotNull
    private String type;
}
