package pe.getsemani.mikhipu.scores.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import pe.getsemani.mikhipu.scores.dto.EvaluationCreateDTO;
import pe.getsemani.mikhipu.scores.dto.EvaluationFilterDTO;
import pe.getsemani.mikhipu.scores.dto.EvaluationResponseDTO;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.scores.mapper.EvaluationMapper;
import pe.getsemani.mikhipu.scores.repository.EvaluationRepository;
import pe.getsemani.mikhipu.scores.specification.EvaluationSpecification;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final CourseRepository courseRepository;
    private final EvaluationMapper evaluationMapper;
    private final ScoreService scoreService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final RepresentativeRepository representativeRepository;

    @Transactional
    public EvaluationResponseDTO create(EvaluationCreateDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        // Unicidad (nombre y fecha en curso)
        boolean exists = evaluationRepository.existsByCourse_IdAndNameAndDate(
                course.getId(), dto.getName(), LocalDate.parse(dto.getDate()));
        if (exists) {
            throw new IllegalArgumentException("Ya existe una evaluación con ese nombre y fecha en el curso");
        }
        Evaluation evaluation = evaluationMapper.fromCreateDto(dto, course);
        evaluationRepository.save(evaluation);
        return evaluationMapper.toResponseDto(evaluation);
    }

    @Transactional
    public EvaluationResponseDTO edit(Long id, EvaluationCreateDTO dto, String username) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // Si el usuario es docente (tiene Teacher)
        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            // Validar que el docente sea el mainTeacher del curso de la evaluación
            if (!evaluation.getCourse().getMainTeacher().getId().equals(teacher.getId())) {
                throw new SecurityException("No tienes permisos para editar esta evaluación");
            }
        }
        // Si no es docente (admin), puede editar cualquier evaluación

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        // Validar unicidad si cambió nombre/fecha
        if (!evaluation.getName().equals(dto.getName()) || !evaluation.getDate().equals(dto.getDate())) {
            boolean exists = evaluationRepository.existsByCourse_IdAndNameAndDate(course.getId(), dto.getName(), LocalDate.parse(dto.getDate()));
            if (exists) throw new IllegalArgumentException("Ya existe una evaluación con ese nombre y fecha en el curso");
        }
        evaluation.setName(dto.getName());
        evaluation.setType(dto.getType());
        evaluation.setWeight(dto.getWeight());
        evaluation.setDate(LocalDate.parse(dto.getDate()));
        evaluation.setMinScore(dto.getMinScore());
        evaluation.setMaxScore(dto.getMaxScore());
        evaluation.setCourse(course);
        evaluationRepository.save(evaluation);
        return evaluationMapper.toResponseDto(evaluation);
    }

    @Transactional
    public void delete(Long id, String username) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // Si el usuario es docente (tiene Teacher)
        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            // Validar que el docente sea el mainTeacher del curso de la evaluación
            if (!evaluation.getCourse().getMainTeacher().getId().equals(teacher.getId())) {
                throw new SecurityException("No tienes permisos para eliminar esta evaluación");
            }
        }
        // Si no es docente (admin), puede eliminar cualquier evaluación

        // Eliminar scores asociados
        scoreService.deleteScoresByEvaluation(evaluation.getId());
        evaluationRepository.delete(evaluation);
    }

    public Page<EvaluationResponseDTO> filterEvaluations(
            EvaluationFilterDTO filter, Pageable pageable, String username
    ) {
        // 1. Si es docente
        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            Set<Long> courseIds = new HashSet<>();
            courseIds.addAll(courseRepository.findByMainTeacher_Id(teacher.getId())
                    .stream().map(Course::getId).toList());
            courseIds.addAll(courseRepository.findByTeachers_Id(teacher.getId())
                    .stream().map(Course::getId).toList());

            // Si hay filtro courseId y no es suyo, devuelve vacío
            if (filter.getCourseId() != null && !courseIds.contains(filter.getCourseId())) {
                return Page.empty(pageable);
            }

            // Aquí la magia: specification adicional para in (sin modificar EvaluationSpecification)
            Specification<Evaluation> courseIdsSpec = (root, query, cb) -> root.get("course").get("id").in(courseIds);

            Specification<Evaluation> spec = courseIdsSpec
                    .and(EvaluationSpecification.hasYear(filter.getYear()))
                    .and(EvaluationSpecification.hasQuarter(filter.getQuarter()))
                    .and(EvaluationSpecification.hasType(filter.getType()))
                    .and(EvaluationSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));

            return evaluationRepository.findAll(spec, pageable)
                    .map(evaluationMapper::toResponseDto);
        }

        // 2. Si es estudiante
        Optional<Student> studentOpt = studentRepository.findByPerson_User_Username(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            // Encuentra todos los cursos en los que participa el estudiante
            List<Course> studentCourses = courseRepository.findByStudents_Id(student.getId());
            Set<Long> courseIds = studentCourses.stream().map(Course::getId).collect(Collectors.toSet());

            // Si hay filtro por curso y no es suyo, devuelve vacío
            if (filter.getCourseId() != null && !courseIds.contains(filter.getCourseId())) {
                return Page.empty(pageable);
            }

            // Specification por cursos
            Specification<Evaluation> courseIdsSpec = (root, query, cb) ->
                    courseIds.isEmpty() ? cb.disjunction() : root.get("course").get("id").in(courseIds);

            Specification<Evaluation> spec = courseIdsSpec
                    .and(EvaluationSpecification.hasYear(filter.getYear()))
                    .and(EvaluationSpecification.hasQuarter(filter.getQuarter()))
                    .and(EvaluationSpecification.hasType(filter.getType()))
                    .and(EvaluationSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));

            return evaluationRepository.findAll(spec, pageable)
                    .map(evaluationMapper::toResponseDto);
        }

        // 3. Si es apoderado
        Optional<Representative> repOpt = representativeRepository.findByPerson_User_Username(username);
        if (repOpt.isPresent()) {
            Representative rep = repOpt.get();
            // Encuentra todos los cursos de los hijos del apoderado
            Set<Student> children = rep.getStudents();
            Set<Long> courseIds = new HashSet<>();
            for (Student s : children) {
                courseIds.addAll(
                        courseRepository.findByStudents_Id(s.getId())
                                .stream().map(Course::getId).toList()
                );
            }

            if (filter.getCourseId() != null && !courseIds.contains(filter.getCourseId())) {
                return Page.empty(pageable);
            }

            Specification<Evaluation> courseIdsSpec = (root, query, cb) ->
                    courseIds.isEmpty() ? cb.disjunction() : root.get("course").get("id").in(courseIds);

            Specification<Evaluation> spec = courseIdsSpec
                    .and(EvaluationSpecification.hasYear(filter.getYear()))
                    .and(EvaluationSpecification.hasQuarter(filter.getQuarter()))
                    .and(EvaluationSpecification.hasType(filter.getType()))
                    .and(EvaluationSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()));

            return evaluationRepository.findAll(spec, pageable)
                    .map(evaluationMapper::toResponseDto);
        }

        // 4. Admin u otro (full access con todos los filtros)
        Specification<Evaluation> spec = Specification.where(EvaluationSpecification.hasCourseId(filter.getCourseId()))
                .and(EvaluationSpecification.hasYear(filter.getYear()))
                .and(EvaluationSpecification.hasQuarter(filter.getQuarter()))
                .and(EvaluationSpecification.hasType(filter.getType()))
                .and(EvaluationSpecification.hasDateBetween(filter.getStartDate(), filter.getEndDate()))
                .and(EvaluationSpecification.hasTeacherId(filter.getTeacherId()));

        return evaluationRepository.findAll(spec, pageable)
                .map(evaluationMapper::toResponseDto);
    }



    public EvaluationResponseDTO getDetails(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        return evaluationMapper.toResponseDto(evaluation);
    }
}
