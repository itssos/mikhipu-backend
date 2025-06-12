package pe.getsemani.mikhipu.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCreateDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.mapper.TeacherMapper;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.persons.teacher.service.TeacherService;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.mapper.UserMapper;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private PersonRepository personRepository;
    @Mock private TeacherMapper teacherMapper;
    @Mock private PersonMapper personMapper;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RoleService roleService;
    @Mock private CourseRepository courseRepository;
    @Mock private StudentMapper studentMapper;
    @Mock private StudentRepository studentRepository;
    @Mock private RepresentativeRepository representativeRepository;

    @InjectMocks private TeacherService teacherService;

    private TeacherCreateDTO teacherDTO;
    private Person person;
    private Teacher teacher;
    private TeacherResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        teacherDTO = new TeacherCreateDTO();
        PersonCreateDTO personDTO = new PersonCreateDTO();
        personDTO.setDni("12345678");

        // MOCKEA EL USER
        var userDTO = new pe.getsemani.mikhipu.user.dto.UserCreateDTO();
        userDTO.setUsername("user");
        userDTO.setPassword("pass");
        userDTO.setEmail("mail@mail.com");
        userDTO.setRole("DOCENTE");
        personDTO.setUser(userDTO);

        teacherDTO.setCode("DOC123");
        teacherDTO.setPerson(personDTO);

        person = new Person();
        teacher = new Teacher();
        responseDTO = new TeacherResponseDTO();
    }

    @Test
    @DisplayName("Debe crear un docente nuevo si no existe por DNI")
    void createTeacher_newPerson_success() {
        when(personRepository.findByDni("12345678")).thenReturn(Optional.empty());
        when(personMapper.fromCreateDto(teacherDTO.getPerson())).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);
        when(teacherRepository.existsByCode("DOC123")).thenReturn(false);
        when(teacherMapper.toEntity(teacherDTO, person)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        TeacherResponseDTO result = teacherService.create(teacherDTO);

        assertNotNull(result);
        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código del docente ya existe")
    void createTeacher_duplicateCode() {
        when(teacherRepository.existsByCode("DOC123")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> teacherService.create(teacherDTO));
    }

    @Test
    @DisplayName("Debe devolver todos los docentes (admin u otro rol)")
    void findAllTeachers_adminSuccess() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        TeacherResponseDTO responseDTO = new TeacherResponseDTO();
        responseDTO.setId(1L);

        // Admin: ninguno de los anteriores retorna present
        when(teacherRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
        when(studentRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
        when(representativeRepository.findByPerson_User_Username("admin")).thenReturn(Optional.empty());
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        // Act
        List<TeacherResponseDTO> result = teacherService.findAll("admin");

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("Debe devolver solo a sí mismo si es docente")
    void findAllTeachers_docenteSoloElMismo() {
        Teacher teacher = new Teacher();
        teacher.setId(2L);
        TeacherResponseDTO responseDTO = new TeacherResponseDTO();
        responseDTO.setId(2L);

        when(teacherRepository.findByPerson_User_Username("docente")).thenReturn(Optional.of(teacher));
        // El resto no interesa porque corta ahí
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        List<TeacherResponseDTO> result = teacherService.findAll("docente");

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    @DisplayName("Debe devolver docentes de los cursos del estudiante")
    void findAllTeachers_estudianteVerDocentesCursos() {
        Student student = new Student();
        student.setId(3L);

        Teacher teacher1 = new Teacher(); teacher1.setId(101L);
        Teacher teacher2 = new Teacher(); teacher2.setId(102L);
        Course course1 = new Course();
        course1.setMainTeacher(teacher1);
        course1.setTeachers(Set.of(teacher2));

        TeacherResponseDTO dto1 = new TeacherResponseDTO(); dto1.setId(101L);
        TeacherResponseDTO dto2 = new TeacherResponseDTO(); dto2.setId(102L);

        when(teacherRepository.findByPerson_User_Username("estu")).thenReturn(Optional.empty());
        when(studentRepository.findByPerson_User_Username("estu")).thenReturn(Optional.of(student));
        when(courseRepository.findByStudents_Id(3L)).thenReturn(List.of(course1));
        when(teacherMapper.toDto(teacher1)).thenReturn(dto1);
        when(teacherMapper.toDto(teacher2)).thenReturn(dto2);

        List<TeacherResponseDTO> result = teacherService.findAll("estu");

        assertEquals(2, result.size());
        Set<Long> ids = result.stream().map(TeacherResponseDTO::getId).collect(Collectors.toSet());
        assertTrue(ids.contains(101L));
        assertTrue(ids.contains(102L));
    }

    @Test
    @DisplayName("Debe actualizar un docente existente")
    void updateTeacher_success() {
        // Configurar usuario actual
        User user = new User();
        user.setUsername("olduser");
        user.setEmail("old@mail.com");
        user.setPassword("pass");
        user.setRole(new Role());

        // Configurar entidad existente
        person.setUser(user);
        teacher.setPerson(person);

        // DTOs para actualización
        PersonCreateDTO pDto = new PersonCreateDTO();
        pDto.setDni("12345678");
        pDto.setFirstName("NuevoNombre");
        pDto.setLastName("Apellido");

        // DTO de usuario correcto (UserCreateDTO)
        var userDto = new pe.getsemani.mikhipu.user.dto.UserCreateDTO();
        userDto.setUsername("newuser");
        userDto.setEmail("new@mail.com");
        userDto.setPassword("newpass");
        userDto.setRole(String.valueOf(RoleType.DOCENTE)); // O el valor que uses

        pDto.setUser(userDto);
        teacherDTO.setPerson(pDto);

        // Stubs
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleService.getRoleByName(String.valueOf(RoleType.DOCENTE))).thenReturn(new Role());
        when(personRepository.save(any())).thenReturn(person);
        when(teacherRepository.save(any())).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        // Ejecutar
        TeacherResponseDTO result = teacherService.update(1L, teacherDTO);

        // Verificar
        assertNotNull(result);
        assertEquals("NuevoNombre", teacher.getPerson().getFirstName());
        verify(userRepository).existsByUsername("newuser");
        verify(personRepository).save(person);
        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("Debe eliminar un docente y sus entidades relacionadas")
    void deleteTeacher_success() {
        User user = new User();
        person.setUser(user);
        teacher.setPerson(person);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.delete(1L);

        verify(teacherRepository).delete(teacher);
        verify(personRepository).delete(person);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no se encuentra el docente")
    void deleteTeacher_notFound() {
        when(teacherRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> teacherService.delete(100L));
    }

    @Test
    @DisplayName("Debe obtener docente por ID")
    void findTeacherById_success() {
        when(teacherRepository.findById(5L)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        TeacherResponseDTO result = teacherService.findById(5L);

        assertNotNull(result);
        verify(teacherRepository).findById(5L);
    }

    @Test
    @DisplayName("Debe obtener lista de estudiantes enseñados por docente")
    void getStudentsByTeacherId_success() {
        Student student = new Student();
        StudentResponseDTO studentDTO = new StudentResponseDTO();

        when(courseRepository.findStudentsByTeacherId(7L)).thenReturn(List.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDTO);

        List<StudentResponseDTO> result = teacherService.getStudentsTaughtByTeacher(7L);

        assertEquals(1, result.size());
    }
}
