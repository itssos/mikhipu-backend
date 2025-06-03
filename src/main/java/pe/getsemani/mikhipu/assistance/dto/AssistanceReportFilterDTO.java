package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;

import java.time.LocalDate;

@Data
public class AssistanceReportFilterDTO {

    @Schema(description = "ID del estudiante para filtrar los registros", example = "123")
    private Long studentId;

    @Schema(description = "Filtrar por estado de entrada (PRESENTE, TARDANZA, AUSENTE, NO_MARCADA)", example = "PRESENTE")
    private AssistanceEntryStatus entryStatus;

    @Schema(description = "Filtrar por estado de salida (SALIDA_REGULAR, SALIDA_ANTICIPADA, NO_MARCADA)", example = "SALIDA_REGULAR")
    private AssistanceExitStatus exitStatus;

    @Schema(description = "Fecha de inicio del rango (inclusive)", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del rango (inclusive)", example = "2025-06-30")
    private LocalDate endDate;

    @Schema(description = "Grado del estudiante", example = "1")
    private Integer grade;

    @Schema(description = "Seccion del estudiante", example = "A")
    private Section section;

    @Schema(description = "Nivel del estudiante", example = "PRIMARIA")
    private SchoolLevel schoolLevel;

    @Schema(description = "ID del curso", example = "5")
    private Long courseId;
}
