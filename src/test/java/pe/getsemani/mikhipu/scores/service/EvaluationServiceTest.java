package pe.getsemani.mikhipu.scores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.scores.dto.*;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.scores.mapper.EvaluationMapper;
import pe.getsemani.mikhipu.scores.repository.EvaluationRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EvaluationMapper evaluationMapper;
    @Mock
    private ScoreService scoreService;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private RepresentativeRepository representativeRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // === TESTS DE CAJA NEGRA ===
    @Test
    @DisplayName("No debe crear evaluación si ya existe otra con mismo nombre y fecha en el curso")
    void createEvaluation_throwsException_whenDuplicate() {
        EvaluationCreateDTO dto = new EvaluationCreateDTO();
        dto.setCourseId(1L);
        dto.setName("Parcial");
        dto.setDate("2025-06-10");

        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(evaluationRepository.existsByCourse_IdAndNameAndDate(1L, "Parcial", LocalDate.parse("2025-06-10")))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> evaluationService.create(dto));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el curso no existe al crear")
    void createEvaluation_throwsNotFound_whenCourseMissing() {
        EvaluationCreateDTO dto = new EvaluationCreateDTO();
        dto.setCourseId(404L);

        when(courseRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluationService.create(dto));
    }

    // === TEST DE CAJA BLANCA: Editar solo si es docente dueño ===
    @Test
    @DisplayName("Editar evaluación solo si el docente es el dueño")
    void editEvaluation_onlyMainTeacher() {
        // Mocks
        Teacher teacher = new Teacher();
        teacher.setId(123L);

        Course course = new Course();
        course.setId(1L);
        course.setMainTeacher(teacher);

        Evaluation eval = new Evaluation();
        eval.setId(10L);
        eval.setCourse(course);
        eval.setName("Parcial");
        eval.setDate(LocalDate.of(2025, 6, 10));

        EvaluationCreateDTO dto = new EvaluationCreateDTO();
        dto.setCourseId(1L);
        dto.setName("Parcial");
        dto.setDate("2025-06-10");

        when(evaluationRepository.findById(10L)).thenReturn(Optional.of(eval));
        when(teacherRepository.findByPerson_User_Username("profesor")).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(evaluationRepository.existsByCourse_IdAndNameAndDate(1L, "Parcial", LocalDate.parse("2025-06-10")))
                .thenReturn(false);

        EvaluationResponseDTO respDTO = new EvaluationResponseDTO();
        when(evaluationMapper.toResponseDto(any())).thenReturn(respDTO);

        // Debe editar correctamente
        assertDoesNotThrow(() -> evaluationService.edit(10L, dto, "profesor"));
    }

    @Test
    @DisplayName("Editar evaluación falla si el docente NO es dueño")
    void editEvaluation_failsIfNotOwner() {
        Teacher teacher = new Teacher();
        teacher.setId(123L);

        Teacher another = new Teacher();
        another.setId(999L);

        Course course = new Course();
        course.setId(1L);
        course.setMainTeacher(another);

        Evaluation eval = new Evaluation();
        eval.setId(10L);
        eval.setCourse(course);

        when(evaluationRepository.findById(10L)).thenReturn(Optional.of(eval));
        when(teacherRepository.findByPerson_User_Username("profesor")).thenReturn(Optional.of(teacher));

        EvaluationCreateDTO dto = new EvaluationCreateDTO();
        dto.setCourseId(1L);

        assertThrows(SecurityException.class, () -> evaluationService.edit(10L, dto, "profesor"));
    }

    // === TEST DE CAJA NEGRA: Filtrado para docente, estudiante, apoderado y admin ===

    @Nested
    class FilterEvaluationsTests {

        @Test
        void docenteFiltraSoloSusCursos() {
            Teacher teacher = new Teacher();
            teacher.setId(1L);
            Course c1 = new Course();
            c1.setId(10L);
            Course c2 = new Course();
            c2.setId(11L);

            when(teacherRepository.findByPerson_User_Username("docente")).thenReturn(Optional.of(teacher));
            when(courseRepository.findByMainTeacher_Id(1L)).thenReturn(List.of(c1));
            when(courseRepository.findByTeachers_Id(1L)).thenReturn(List.of(c2));

            EvaluationFilterDTO filter = new EvaluationFilterDTO();
            Pageable pageable = PageRequest.of(0, 10);

            Page<Evaluation> mockPage = new PageImpl<>(List.of());
            when(evaluationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

            Page<EvaluationResponseDTO> result = evaluationService.filterEvaluations(filter, pageable, "docente");
            assertNotNull(result);
        }

        @Test
        void estudianteFiltraSoloSusCursos() {
            Student student = new Student();
            student.setId(7L);

            Course c1 = new Course(); c1.setId(99L);

            when(studentRepository.findByPerson_User_Username("estudiante")).thenReturn(Optional.of(student));
            when(courseRepository.findByStudents_Id(7L)).thenReturn(List.of(c1));

            EvaluationFilterDTO filter = new EvaluationFilterDTO();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Evaluation> mockPage = new PageImpl<>(List.of());
            when(evaluationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

            Page<EvaluationResponseDTO> result = evaluationService.filterEvaluations(filter, pageable, "estudiante");
            assertNotNull(result);
        }

        @Test
        void apoderadoFiltraCursosDeHijos() {
            Representative rep = new Representative();
            Student child = new Student();
            child.setId(99L);
            rep.setStudents(Set.of(child));
            when(representativeRepository.findByPerson_User_Username("apoderado")).thenReturn(Optional.of(rep));
            Course c1 = new Course(); c1.setId(2L);
            when(courseRepository.findByStudents_Id(99L)).thenReturn(List.of(c1));

            EvaluationFilterDTO filter = new EvaluationFilterDTO();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Evaluation> mockPage = new PageImpl<>(List.of());
            when(evaluationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

            Page<EvaluationResponseDTO> result = evaluationService.filterEvaluations(filter, pageable, "apoderado");
            assertNotNull(result);
        }

        @Test
        void adminFiltraTodo() {
            when(teacherRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
            when(studentRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
            when(representativeRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());

            EvaluationFilterDTO filter = new EvaluationFilterDTO();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Evaluation> mockPage = new PageImpl<>(List.of());
            when(evaluationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

            Page<EvaluationResponseDTO> result = evaluationService.filterEvaluations(filter, pageable, "admin");
            assertNotNull(result);
        }
    }

    // === TEST UNITARIO DE DELETE ===
    @Test
    void deleteEvaluation_docenteNoEsDueño_throwSecurity() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        Teacher otro = new Teacher();
        otro.setId(2L);

        Course course = new Course();
        course.setId(3L);
        course.setMainTeacher(otro);

        Evaluation eval = new Evaluation();
        eval.setId(9L);
        eval.setCourse(course);

        when(evaluationRepository.findById(9L)).thenReturn(Optional.of(eval));
        when(teacherRepository.findByPerson_User_Username("docente")).thenReturn(Optional.of(teacher));

        assertThrows(SecurityException.class, () -> evaluationService.delete(9L, "docente"));
    }

    // Caja blanca: admin puede borrar cualquiera
    @Test
    void deleteEvaluation_admin_ok() {
        Course course = new Course();
        course.setId(3L);

        Evaluation eval = new Evaluation();
        eval.setId(9L);
        eval.setCourse(course);

        when(evaluationRepository.findById(9L)).thenReturn(Optional.of(eval));
        when(teacherRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());

        // No debe lanzar error
        assertDoesNotThrow(() -> evaluationService.delete(9L, "admin"));
    }
}
