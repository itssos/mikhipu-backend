package pe.getsemani.mikhipu.scores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con la información de una calificación registrada")
public class ScoreResponseDTO {

    @Schema(description = "ID de la calificación", example = "101")
    private Long id;

    @Schema(description = "ID del estudiante", example = "42")
    private Long studentId;

    @Schema(description = "Nombre completo del estudiante", example = "Juan Pérez")
    private String studentFullName;

    @Schema(description = "ID de la evaluación", example = "12")
    private Long evaluationId;

    @Schema(description = "Nombre de la evaluación", example = "Examen Parcial")
    private String evaluationName;

    @Schema(description = "Peso de la evaluación", example = "17.5")
    private Double evaluationWeight;

    @Schema(description = "ID del curso", example = "5")
    private Long courseId;

    @Schema(description = "Nombre del curso", example = "Matemática 5to")
    private String courseName;

    @Schema(description = "Valor de la calificación", example = "15.5")
    private Double value;

    @Schema(description = "Fecha de registro de la calificación", example = "2025-06-08T10:15:00")
    private String createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-06-08T12:00:00")
    private String updatedAt;
}