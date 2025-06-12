package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "DTO de filtro para consultar calificaciones con múltiples parámetros")
public class ScoreFilterDTOMe {

    @Schema(description = "ID del curso", example = "10")
    @Min(value = 1, message = "El ID del curso debe ser mayor que 0.")
    private Long courseId;

    @Schema(description = "ID de la evaluación", example = "100")
    @Min(value = 1, message = "El ID de evaluación debe ser mayor que 0.")
    private Long evaluationId;

    @Schema(description = "Año académico (formato: 2025)", example = "2025")
    @Pattern(regexp = "^\\d{4}$", message = "El año debe tener 4 dígitos.")
    private String year;

    @Schema(description = "Trimestre académico (por ejemplo: Q1, Q2, Q3, Q4)", example = "Q2")
    private String quarter;

    @Schema(description = "Fecha de inicio para filtrar (formato: yyyy-MM-dd)", example = "2025-05-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "Fecha de fin para filtrar (formato: yyyy-MM-dd)", example = "2025-06-30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
