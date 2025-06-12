package pe.getsemani.mikhipu.scores.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.scores.dto.RepresentativeScoreHistoryDTO;
import pe.getsemani.mikhipu.scores.dto.ScoreCreateDTO;
import pe.getsemani.mikhipu.scores.dto.ScoreFilterDTOMe;
import pe.getsemani.mikhipu.scores.dto.ScoreHistoryFilterDTO;
import pe.getsemani.mikhipu.scores.dto.ScoreResponseDTO;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.scores.entity.Score;
import pe.getsemani.mikhipu.scores.mapper.ScoreMapper;
import pe.getsemani.mikhipu.scores.repository.EvaluationRepository;
import pe.getsemani.mikhipu.scores.repository.ScoreRepository;
import pe.getsemani.mikhipu.scores.specification.ScoreSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RepresentativeRepository representativeRepository;
    private final ScoreMapper scoreMapper;
    private final TeacherRepository teacherRepository;

    @Transactional
    public ScoreResponseDTO registerScore(ScoreCreateDTO dto) {
        // Validación de existencia de entidades
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        Evaluation evaluation = evaluationRepository.findById(dto.getEvaluationId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        // Validación de rango
        if (dto.getValue() < evaluation.getMinScore() || dto.getValue() > evaluation.getMaxScore()) {
            throw new IllegalArgumentException("La nota debe estar entre " + evaluation.getMinScore() + " y " + evaluation.getMaxScore());
        }
        // Unicidad
        if (scoreRepository.existsByStudentAndEvaluation(student, evaluation)) {
            throw new IllegalArgumentException("Ya existe una nota para este estudiante en esta evaluación");
        }
        // Crear entidad y guardar
        Score score = scoreMapper.fromCreateDto(dto, student, evaluation);
        scoreRepository.save(score);
        return scoreMapper.toResponseDto(score);
    }

    public Page<ScoreResponseDTO> findScoresByStudentWithFilters(
            Long studentId,
            ScoreFilterDTOMe filter,
            Pageable pageable
    ) {
        Specification<Score> spec = Specification
                .where(ScoreSpecification.hasStudentId(studentId))
                .and(ScoreSpecification.hasCourseId(filter.getCourseId()))
                .and(ScoreSpecification.hasEvaluationId(filter.getEvaluationId()))
                .and(ScoreSpecification.hasYear(filter.getYear()))
                .and(ScoreSpecification.hasQuarter(filter.getQuarter()))
                .and(ScoreSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));
        return scoreRepository.findAll(spec, pageable).map(scoreMapper::toResponseDto);
    }

    public Page<RepresentativeScoreHistoryDTO> findScoresOfMyChildren(
            String username,
            ScoreFilterDTOMe filter,
            Pageable pageable
    ) {
        Representative representative = representativeRepository.findByPerson_User_Username(username)
                .orElseThrow(() -> new RuntimeException("No es un representante"));

        List<Student> students = new ArrayList<>(representative.getStudents());

        // Pagina los hijos (students)
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int start = currentPage * pageSize;
        int end = Math.min((start + pageSize), students.size());

        if (start >= end) {
            return new PageImpl<>(Collections.emptyList(), pageable, students.size());
        }

        List<Student> pagedStudents = students.subList(start, end);

        List<RepresentativeScoreHistoryDTO> content = pagedStudents.stream()
                .map(student -> {
                    List<ScoreResponseDTO> scores = this.findScoresByStudentWithFilters(
                            student.getId(), filter, PageRequest.of(0, Integer.MAX_VALUE) // todas las notas del hijo
                    ).getContent();
                    String fullName = scores.isEmpty()
                            ? student.getPerson().getFirstName() + " " + student.getPerson().getLastName()
                            : scores.get(0).getStudentFullName();
                    return new RepresentativeScoreHistoryDTO(student.getId(), fullName, scores);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, students.size());
    }

    public Page<ScoreResponseDTO> getHistory(ScoreHistoryFilterDTO filter, Pageable pageable, String username) {
        // 1. Si es docente, filtra solo por sus estudiantes
        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            // Cursos donde es mainTeacher o está en teachers
            Set<Long> courseIds = new HashSet<>();
            courseIds.addAll(courseRepository.findByMainTeacher_Id(teacher.getId())
                    .stream().map(Course::getId).toList());
            courseIds.addAll(courseRepository.findByTeachers_Id(teacher.getId())
                    .stream().map(Course::getId).toList());

            // Unir todos los estudiantes de esos cursos
            Set<Long> studentIds = courseRepository.findAllById(courseIds)
                    .stream()
                    .flatMap(course -> course.getStudents().stream())
                    .map(Student::getId)
                    .collect(Collectors.toSet());

            // Si filtra por estudiante y no es suyo, no mostrar nada
            if (filter.getStudentId() != null && !studentIds.contains(filter.getStudentId())) {
                return Page.empty(pageable);
            }

            Specification<Score> spec = (root, query, cb) -> root.get("student").get("id").in(studentIds);
            spec = spec
                    .and(ScoreSpecification.hasStudentId(filter.getStudentId()))
                    .and(ScoreSpecification.hasCourseId(filter.getCourseId()))
                    .and(ScoreSpecification.hasEvaluationId(filter.getEvaluationId()))
                    .and(ScoreSpecification.hasYear(filter.getYear()))
                    .and(ScoreSpecification.hasQuarter(filter.getQuarter()))
                    .and(ScoreSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));

            return scoreRepository.findAll(spec, pageable)
                    .map(scoreMapper::toResponseDto);
        }

        // 2. Para otros roles, acceso normal (admin, apoderado, etc.)
        Specification<Score> spec = Specification.where(ScoreSpecification.hasStudentId(filter.getStudentId()))
                .and(ScoreSpecification.hasCourseId(filter.getCourseId()))
                .and(ScoreSpecification.hasEvaluationId(filter.getEvaluationId()))
                .and(ScoreSpecification.hasYear(filter.getYear()))
                .and(ScoreSpecification.hasQuarter(filter.getQuarter()))
                .and(ScoreSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));
        return scoreRepository.findAll(spec, pageable)
                .map(scoreMapper::toResponseDto);
    }

    public Double getWeightedAverage(Long studentId, Long courseId, String year, String quarter) {
        List<Score> scores = scoreRepository.findAll(
                Specification.where(ScoreSpecification.hasStudentId(studentId))
                        .and(ScoreSpecification.hasCourseId(courseId))
                        .and(ScoreSpecification.hasYear(year))
                        .and(ScoreSpecification.hasQuarter(quarter))
        );
        Map<Evaluation, Score> byEval = scores.stream()
                .collect(Collectors.toMap(Score::getEvaluation, s -> s, (s1, s2) -> s1));
        double totalWeight = 0.0;
        double weightedSum = 0.0;
        for (Score s : byEval.values()) {
            double weight = s.getEvaluation().getWeight();
            weightedSum += s.getValue() * (weight / 100.0);
            totalWeight += weight / 100.0;
        }
        return totalWeight > 0 ? weightedSum / totalWeight : null;
    }

    @Transactional
    public void deleteScoresByEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        scoreRepository.deleteByEvaluation(evaluation);
    }

}
