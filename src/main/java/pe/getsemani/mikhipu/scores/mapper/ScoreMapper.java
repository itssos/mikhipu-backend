package pe.getsemani.mikhipu.scores.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.scores.dto.ScoreCreateDTO;
import pe.getsemani.mikhipu.scores.dto.ScoreResponseDTO;
import pe.getsemani.mikhipu.scores.entity.Score;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.persons.student.entity.Student;

@Component
public class ScoreMapper {

    // Convierte de ScoreCreateDTO a entidad Score (para registrar una nota)
    public Score fromCreateDto(ScoreCreateDTO dto, Student student, Evaluation evaluation) {
        if (dto == null || student == null || evaluation == null) return null;
        return Score.builder()
                .student(student)
                .evaluation(evaluation)
                .value(dto.getValue())
                .build();
    }

    // Convierte entidad Score a ScoreResponseDTO (para mostrar)
    public ScoreResponseDTO toResponseDto(Score entity) {
        Evaluation eval = entity.getEvaluation();
        Course course = eval.getCourse();

        return ScoreResponseDTO.builder()
                .id(entity.getId())
                .studentId(entity.getStudent().getId())
                .studentFullName(entity.getStudent().getPerson().getFirstName() + " " + entity.getStudent().getPerson().getLastName())
                .evaluationId(eval.getId())
                .evaluationName(eval.getName())
                .evaluationWeight(eval.getWeight())
                .courseId(course.getId())
                .courseName(course.getName())
                .value(entity.getValue())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }
}