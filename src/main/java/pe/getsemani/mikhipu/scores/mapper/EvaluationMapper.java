package pe.getsemani.mikhipu.scores.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.scores.dto.EvaluationCreateDTO;
import pe.getsemani.mikhipu.scores.dto.EvaluationResponseDTO;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.course.entity.Course;

import java.time.LocalDate;

@Component
public class EvaluationMapper {

    // Convierte EvaluationCreateDTO a entidad Evaluation (para crear evaluación)
    public Evaluation fromCreateDto(EvaluationCreateDTO dto, Course course) {
        if (dto == null || course == null) return null;
        return Evaluation.builder()
                .course(course)
                .name(dto.getName())
                .type(dto.getType())
                .weight(dto.getWeight())
                .date(LocalDate.parse(dto.getDate()))
                .minScore(dto.getMinScore())
                .maxScore(dto.getMaxScore())
                .build();
    }

    // Convierte entidad Evaluation a EvaluationResponseDTO
    public EvaluationResponseDTO toResponseDto(Evaluation entity) {
        if (entity == null) return null;
        return EvaluationResponseDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourse().getId())
                .courseName(entity.getCourse().getName())
                .name(entity.getName())
                .type(entity.getType())
                .weight(entity.getWeight())
                .date(String.valueOf(entity.getDate()))
                .minScore(entity.getMinScore())
                .maxScore(entity.getMaxScore())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .updatedAt(String.valueOf(entity.getUpdatedAt()))
                .build();
    }
}