package pe.getsemani.mikhipu.person.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.entity.Teacher;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.StudentMapper;
import pe.getsemani.mikhipu.person.mapper.TeacherMapper;
import pe.getsemani.mikhipu.person.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.person.repository.TeacherRepository;
import pe.getsemani.mikhipu.persons.teacher.service.TeacherService;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.mapper.UserMapper;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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
        when(personRepository.findByDni("12345678")).thenReturn(Optional.of(person));
        when(teacherRepository.existsByCode("DOC123")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> teacherService.create(teacherDTO));
    }

    @Test
    @DisplayName("Debe devolver todos los docentes")
    void findAllTeachers_success() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(responseDTO);

        List<TeacherResponseDTO> result = teacherService.findAll();

        assertEquals(1, result.size());
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
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no se encuentra el docente")
    void deleteTeacher_notFound() {
        when(teacherRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> teacherService.delete(100L));
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
