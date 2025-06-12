package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "DTO de filtro para consultar evaluaciones")
public class EvaluationFilterDTO {

    @Schema(description = "ID del curso", example = "15")
    @Min(value = 1, message = "El ID del curso debe ser mayor a 0")
    private Long courseId;

    @Schema(description = "ID del docente principal", example = "22")
    @Min(value = 1, message = "El ID del docente debe ser mayor a 0")
    private Long teacherId;

    @Schema(description = "Año académico (ejemplo: 2025)", example = "2025")
    private String year;

    @Schema(description = "Trimestre académico (por ejemplo: Q1, Q2, Q3, Q4)", example = "Q1")
    private String quarter;

    @Schema(description = "Tipo de evaluación", example = "EXAM")
    private String type;

    @Schema(description = "Fecha de inicio", example = "2025-06-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "Fecha de fin", example = "2025-06-15")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
