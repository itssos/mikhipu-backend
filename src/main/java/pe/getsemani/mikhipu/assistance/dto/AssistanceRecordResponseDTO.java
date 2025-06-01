package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDateTime;

@Data
public class AssistanceRecordResponseDTO {

    @Schema(description = "ID del registro de asistencia", example = "501")
    private Long id;

    @Schema(description = "ID del estudiante", example = "123")
    private Long studentId;

    @Schema(description = "ID de la sesión", example = "10")
    private Long sessionId;

    @Schema(description = "Hora registrada de entrada", example = "2025-06-01T08:10:00")
    private LocalDateTime entryMarkedAt;

    @Schema(description = "Estado de la entrada", example = "TARDANZA")
    private AssistanceEntryStatus entryStatus;

    @Schema(description = "Hora registrada de salida", example = "2025-06-01T13:05:00")
    private LocalDateTime exitMarkedAt;

    @Schema(description = "Estado de la salida", example = "SALIDA_REGULAR")
    private AssistanceExitStatus exitStatus;

}
