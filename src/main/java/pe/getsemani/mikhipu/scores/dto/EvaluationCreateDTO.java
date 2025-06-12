package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.scores.enums.EvaluationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear una evaluación")
public class EvaluationCreateDTO {

    @Schema(description = "ID del curso asociado", example = "7", required = true)
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;

    @Schema(description = "Nombre de la evaluación", example = "Examen Parcial", required = true)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;

    @Schema(
            description = "Tipo de evaluación (EXAM, PROJECT, QUIZ, etc.)",
            example = "EXAM",
            required = true,
            allowableValues = {"EXAM", "PROJECT", "QUIZ", "ASSIGNMENT", "PRESENTATION", "OTHER"}
    )
    @NotNull(message = "El tipo de evaluación es obligatorio")
    private EvaluationType type;

    @Schema(description = "Peso de la evaluación en el promedio (%)", example = "40", required = true)
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "1.0", message = "El peso mínimo es 1")
    @DecimalMax(value = "100.0", message = "El peso máximo es 100")
    private Double weight;

    @Schema(description = "Fecha de la evaluación (yyyy-MM-dd)", example = "2025-06-10", required = true)
    @NotBlank(message = "La fecha es obligatoria")
    private String date;

    @Schema(description = "Nota mínima permitida", example = "0", required = true)
    @NotNull(message = "La nota mínima es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima no puede ser menor que 0")
    private Double minScore;

    @Schema(description = "Nota máxima permitida", example = "20", required = true)
    @NotNull(message = "La nota máxima es obligatoria")
    @DecimalMax(value = "20.0", message = "La nota máxima no puede ser mayor que 20")
    private Double maxScore;
}