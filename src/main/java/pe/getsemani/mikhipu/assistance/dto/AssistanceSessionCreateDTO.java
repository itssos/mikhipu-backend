package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AssistanceSessionCreateDTO {

    @Schema(description = "Hora de inicio para marcar la entrada", example = "08:00", required = true)
    @NotNull
    private LocalTime startEntryTime;

    @Schema(description = "Hora de fin para marcar la entrada", example = "08:30", required = true)
    @NotNull
    private LocalTime endEntryTime;

    @Schema(description = "Hora de inicio para marcar la salida", example = "13:00", required = true)
    @NotNull
    private LocalTime startExitTime;

    @Schema(description = "Hora de fin para marcar la salida", example = "13:30", required = true)
    @NotNull
    private LocalTime endExitTime;

    @Schema(description = "Fecha y hora límite para editar asistencia", example = "2025-06-01T14:00:00", required = true)
    @NotNull
    @FutureOrPresent(message = "La fecha límite debe ser actual o futura.")
    private LocalDateTime attendanceDeadline;

    @Schema(description = "Indica si esta configuración está activa", example = "true")
    private Boolean active = true;
}
