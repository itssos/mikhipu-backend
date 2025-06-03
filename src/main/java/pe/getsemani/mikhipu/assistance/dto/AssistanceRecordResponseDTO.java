package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AssistanceRecordResponseDTO {

    @Schema(description = "ID del registro de asistencia", example = "501")
    private Long id;

    @Schema(description = "ID del estudiante", example = "123")
    private Long studentId;

    @Schema(description = "Nombre del estudiante", example = "Pepe")
    private String firstName;

    @Schema(description = "Apellido del estudiante", example = "Juarez")
    private String lastName;

    @Schema(description = "Fecha de la asistencia", example = "2025-06-01")
    private LocalDate date;

    @Schema(description = "Hora registrada de entrada", example = "2025-06-01T08:10:00")
    private LocalDateTime entryMarkedAt;

    @Schema(description = "Estado de la entrada", example = "TARDANZA")
    private AssistanceEntryStatus entryStatus;

    @Schema(description = "Hora registrada de salida", example = "2025-06-01T13:05:00")
    private LocalDateTime exitMarkedAt;

    @Schema(description = "Estado de la salida", example = "SALIDA_REGULAR")
    private AssistanceExitStatus exitStatus;

    @Schema(description = "Grado del estudiante", example = "1")
    private Integer grade;

    @Schema(description = "Seccion del estudiante", example = "A")
    private Section section;

    @Schema(description = "Nivel del estudiante", example = "PRIMARIA")
    private SchoolLevel schoolLevel;

    @Schema(description = "Indica si el registro fue editado manualmente", example = "false")
    private Boolean edited;

}
