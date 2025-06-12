package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar una calificación de estudiante en una evaluación")
public class ScoreCreateDTO {

    @Schema(description = "ID del estudiante", example = "42", required = true)
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;

    @Schema(description = "ID de la evaluación", example = "12", required = true)
    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long evaluationId;

    @Schema(description = "Valor de la calificación", example = "15.5", required = true)
    @NotNull(message = "El valor de la calificación es obligatorio")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor que 0")
    @DecimalMax(value = "20.0", message = "La calificación no puede ser mayor que 20")
    private Double value;
}