package pe.getsemani.mikhipu.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AssistanceStatisticsDTO {

    @Schema(description = "ID del estudiante", example = "123")
    private Long studentId;

    @Schema(description = "Nombre completo del estudiante", example = "Carlos Pérez Gómez")
    private String studentFullName;

    @Schema(description = "Total de sesiones dentro del rango", example = "20")
    private Long totalSessions;   // Cambia a Long

    @Schema(description = "Número de asistencias marcadas como PRESENTE", example = "15")
    private Long presentes;       // Cambia a Long

    @Schema(description = "Número de asistencias marcadas como TARDANZA", example = "3")
    private Long tardanzas;       // Cambia a Long

    @Schema(description = "Número de asistencias marcadas como AUSENTE", example = "2")
    private Long ausencias;       // Cambia a Long

    @Schema(description = "Número de salidas registradas como SALIDA_REGULAR", example = "17")
    private Long salidasRegulares; // Cambia a Long

    @Schema(description = "Número de salidas registradas como SALIDA_ANTICIPADA", example = "3")
    private Long salidasAnticipadas; // Cambia a Long

    @Schema(description = "Porcentaje de asistencia (PRESENTE + TARDANZA) respecto al total", example = "90.0")
    private Double porcentajeAsistencia;

    // Constructor público con el mismo orden y tipos que la query
    public AssistanceStatisticsDTO(
            Long studentId,
            String studentFullName,
            Long totalSessions,
            Long presentes,
            Long tardanzas,
            Long ausencias,
            Long salidasRegulares,
            Long salidasAnticipadas,
            Double porcentajeAsistencia
    ) {
        this.studentId = studentId;
        this.studentFullName = studentFullName;
        this.totalSessions = totalSessions;
        this.presentes = presentes;
        this.tardanzas = tardanzas;
        this.ausencias = ausencias;
        this.salidasRegulares = salidasRegulares;
        this.salidasAnticipadas = salidasAnticipadas;
        this.porcentajeAsistencia = porcentajeAsistencia;
    }
}
