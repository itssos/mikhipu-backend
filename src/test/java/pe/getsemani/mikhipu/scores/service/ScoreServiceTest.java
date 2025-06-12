package pe.getsemani.mikhipu.scores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.scores.dto.*;
import pe.getsemani.mikhipu.scores.entity.*;
import pe.getsemani.mikhipu.scores.mapper.ScoreMapper;
import pe.getsemani.mikhipu.scores.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD + Unitario + Caja Negra + Caja Blanca para ScoreService
 */
class ScoreServiceTest {

    @InjectMocks
    private ScoreService scoreService;

    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private RepresentativeRepository representativeRepository;
    @Mock
    private ScoreMapper scoreMapper;
    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Caja blanca: Docente solo ve historial de notas de sus estudiantes
     * TDD: Demuestra branch principal del filtro de seguridad por docente
     */
    @Test
    @DisplayName("Historial: docente solo ve notas de sus estudiantes")
    void getHistory_docenteFiltraSoloSusEstudiantes() {
        Teacher teacher = new Teacher(); teacher.setId(11L);
        Course c1 = new Course(); c1.setId(21L);
        c1.setMainTeacher(teacher);
        Student s1 = new Student(); s1.setId(31L);
        c1.setStudents(Set.of(s1));
        Score score = new Score(); score.setId(100L); score.setStudent(s1);

        // Mock: docente dueño de curso
        when(teacherRepository.findByPerson_User_Username("docente")).thenReturn(Optional.of(teacher));
        when(courseRepository.findByMainTeacher_Id(11L)).thenReturn(List.of(c1));
        when(courseRepository.findByTeachers_Id(11L)).thenReturn(List.of());
        when(courseRepository.findAllById(Set.of(21L))).thenReturn(List.of(c1));
        when(scoreRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(score)));
        when(scoreMapper.toResponseDto(score)).thenReturn(new ScoreResponseDTO());

        // Act
        Page<ScoreResponseDTO> result = scoreService.getHistory(new ScoreHistoryFilterDTO(), PageRequest.of(0, 10), "docente");

        // Assert
        assertEquals(1, result.getContent().size(), "El docente debería ver la nota de su estudiante");
    }

    /**
     * Caja negra: Docente no puede ver notas de estudiantes de otros cursos
     */
    @Test
    @DisplayName("Historial: si docente filtra por estudiante que NO es suyo, devuelve vacío")
    void getHistory_docenteFiltraEstudianteNoPropio() {
        Teacher teacher = new Teacher(); teacher.setId(11L);
        Course c1 = new Course(); c1.setId(21L);
        c1.setMainTeacher(teacher);
        Student s1 = new Student(); s1.setId(31L);
        c1.setStudents(Set.of(s1));
        when(teacherRepository.findByPerson_User_Username("docente")).thenReturn(Optional.of(teacher));
        when(courseRepository.findByMainTeacher_Id(11L)).thenReturn(List.of(c1));
        when(courseRepository.findByTeachers_Id(11L)).thenReturn(List.of());
        when(courseRepository.findAllById(Set.of(21L))).thenReturn(List.of(c1));

        ScoreHistoryFilterDTO filter = new ScoreHistoryFilterDTO();
        filter.setStudentId(999L); // estudiante ajeno
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        Page<ScoreResponseDTO> result = scoreService.getHistory(filter, pageable, "docente");
        assertTrue(result.getContent().isEmpty(), "No debe mostrar notas ajenas");
    }

    /**
     * Caja negra: Otros roles acceden normalmente al historial de notas
     */
    @Test
    @DisplayName("Historial: para otros roles, aplica filtro general")
    void getHistory_adminOrOtherRole() {
        when(teacherRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
        when(scoreRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ScoreResponseDTO> result = scoreService.getHistory(new ScoreHistoryFilterDTO(), PageRequest.of(0, 10), "admin");
        assertNotNull(result, "Debe retornar un Page (aunque esté vacío)");
    }

    /**
     * Caja blanca + negra: Apoderado obtiene paginación y nombres de estudiantes correctamente.
     */
    @Test
    @DisplayName("findScoresOfMyChildren: pagina correctamente estudiantes e incluye scores")
    void findScoresOfMyChildren_paginadoOk() {
        // Arrange: estudiantes con persona mockeada
        Person person1 = new Person();
        person1.setFirstName("Juan");
        person1.setLastName("Pérez");

        Person person2 = new Person();
        person2.setFirstName("Ana");
        person2.setLastName("Gómez");

        Student s1 = new Student();
        s1.setId(1L);
        s1.setPerson(person1);

        Student s2 = new Student();
        s2.setId(2L);
        s2.setPerson(person2);

        Representative rep = new Representative();
        rep.setStudents(Set.of(s1, s2));

        when(representativeRepository.findByPerson_User_Username("apoderado")).thenReturn(Optional.of(rep));
        when(scoreRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of())); // No hay notas para este hijo
        when(scoreMapper.toResponseDto(any())).thenReturn(new ScoreResponseDTO());

        ScoreFilterDTOMe filter = new ScoreFilterDTOMe();
        Pageable pageable = PageRequest.of(0, 1); // page size 1 para paginación

        // Act
        Page<RepresentativeScoreHistoryDTO> page1 = scoreService.findScoresOfMyChildren("apoderado", filter, pageable);

        // Assert (Caja negra: la paginación, Caja blanca: nombres correctos)
        assertEquals(1, page1.getContent().size(), "Debe devolver solo 1 hijo por página");
        RepresentativeScoreHistoryDTO dto = page1.getContent().get(0);
        assertNotNull(dto.getStudentFullName(), "Debe devolver el nombre completo del estudiante hijo");
        assertTrue(dto.getStudentFullName().equals("Juan Pérez") || dto.getStudentFullName().equals("Ana Gómez"), "Nombre debe coincidir con algún hijo");
    }

    /**
     * Unitario puro, TDD: Registrar score fuera de rango debe lanzar excepción
     */
    @Test
    @DisplayName("No permite registrar score fuera de rango")
    void registerScore_outOfRange_throws() {
        // Arrange
        Student student = new Student(); student.setId(1L);
        Evaluation eval = new Evaluation(); eval.setId(2L);
        eval.setMinScore(10.0); eval.setMaxScore(20.0);

        ScoreCreateDTO dto = new ScoreCreateDTO();
        dto.setStudentId(1L);
        dto.setEvaluationId(2L);
        dto.setValue(9.5); // Menor que el mínimo permitido

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(evaluationRepository.findById(2L)).thenReturn(Optional.of(eval));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> scoreService.registerScore(dto),
                "No debe permitir notas fuera de rango");
    }
}
