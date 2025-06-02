package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssistanceRecordCreateDTO {

    @Schema(description = "ID del estudiante (leído desde el QR)", example = "123", required = true)
    @NotNull
    private Long studentId;

}
