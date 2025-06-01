package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AssistanceSessionResponseDTO {

    @Schema(description = "ID de la sesión", example = "1")
    private Long id;

    @Schema(description = "Fecha de la sesión", example = "2025-06-01")
    private LocalDate date;

    @Schema(description = "Hora de inicio de entrada", example = "08:00")
    private LocalTime startEntryTime;

    @Schema(description = "Hora de fin de entrada", example = "08:30")
    private LocalTime endEntryTime;

    @Schema(description = "Hora de inicio de salida", example = "13:00")
    private LocalTime startExitTime;

    @Schema(description = "Hora de fin de salida", example = "13:30")
    private LocalTime endExitTime;

    @Schema(description = "Fecha y hora límite para edición", example = "2025-06-01T14:00:00")
    private LocalDateTime attendanceDeadline;
}
