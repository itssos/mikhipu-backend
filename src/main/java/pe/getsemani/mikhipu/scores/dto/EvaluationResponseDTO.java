package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.scores.enums.EvaluationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta de una evaluación")
public class EvaluationResponseDTO {

    @Schema(description = "ID de la evaluación", example = "12")
    private Long id;

    @Schema(description = "ID del curso", example = "7")
    private Long courseId;

    @Schema(description = "Nombre del curso", example = "Matemática")
    private String courseName;

    @Schema(description = "Nombre de la evaluación", example = "Examen Parcial")
    private String name;

    @Schema(
            description = "Tipo de evaluación (EXAM, PROJECT, QUIZ, etc.)",
            example = "EXAM"
    )
    private EvaluationType type;

    @Schema(description = "Peso (%) de la evaluación", example = "40")
    private Double weight;

    @Schema(description = "Fecha de la evaluación (yyyy-MM-dd)", example = "2025-06-10")
    private String date;

    @Schema(description = "Nota mínima permitida", example = "0")
    private Double minScore;

    @Schema(description = "Nota máxima permitida", example = "20")
    private Double maxScore;

    @Schema(description = "Fecha de creación", example = "2025-06-08T10:00:00")
    private String createdAt;

    @Schema(description = "Fecha de actualización", example = "2025-06-08T12:00:00")
    private String updatedAt;
}