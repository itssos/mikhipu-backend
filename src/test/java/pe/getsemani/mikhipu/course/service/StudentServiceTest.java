package pe.getsemani.mikhipu.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.assistance.repository.AssistanceRecordRepository;
import pe.getsemani.mikhipu.persons.representative.dto.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.persons.representative.projection.RepresentativeBasicProjection;
import pe.getsemani.mikhipu.persons.student.dto.StudentCreateDTO;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.person.enums.Gender;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepresentativeRepository;
import pe.getsemani.mikhipu.persons.person.service.PersonService;
import pe.getsemani.mikhipu.persons.student.service.StudentService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private RepresentativeRepository representativeRepository;
    @Mock
    private PersonService personService;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private StudentRepresentativeRepository studentRepresentativeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RepresentativeBasicProjection projection;
    @Mock
    private PersonRepository personRepository;
    @Mock private AssistanceRecordRepository assistanceRecordRepository;


    @InjectMocks
    private StudentService studentService;

    private StudentCreateDTO dto;
    private Person person;
    private Student student;
    private StudentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Iniciar objetos base
        person = new Person();
        student = new Student();
        responseDTO = new StudentResponseDTO();

        dto = new StudentCreateDTO();
        PersonCreateDTO pDto = new PersonCreateDTO();
        pDto.setFirstName("Juan");
        pDto.setLastName("Perez");
        pDto.setDni("12345678");
        pDto.setBirthDate(java.time.LocalDate.of(2005,1,1));
        pDto.setGender(Gender.MASCULINO);
        pDto.setAddress("Av. Siempre Viva 123");
        pDto.setPhone("987654321");
        dto.setPerson(pDto);
        dto.setGrade(10);
        dto.setSection(Section.A);
        dto.setSchoolLevel(SchoolLevel.PRIMARIA);
        dto.setRepresentativeIds(Set.of(1L));
    }

    @Test
    @DisplayName("Debe crear un estudiante correctamente con representante")
    void createStudent_success() {
        when(studentMapper.fromCreateDto(dto)).thenReturn(student);
        when(personMapper.fromCreateDto(dto.getPerson())).thenReturn(person);
        when(personService.saveRaw(person)).thenReturn(person);
        when(representativeRepository.findAllById(dto.getRepresentativeIds()))
                .thenReturn(List.of(new Representative()));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.createStudent(dto);

        assertNotNull(result);
        verify(personService).saveRaw(person);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Debe actualizar un estudiante existente correctamente")
    void updateStudent_success() {
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        // Pre-cargar datos previos en student.person
        person = new Person(); student.setPerson(person);
        when(personService.saveRaw(person)).thenReturn(person);
        when(representativeRepository.findAllById(dto.getRepresentativeIds()))
                .thenReturn(List.of(new Representative()));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.updateStudent(id, dto);

        assertNotNull(result);
        assertEquals(10, student.getGrade());
        verify(personService).saveRaw(person);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Debe obtener un estudiante por ID exitosamente")
    void getStudentById_success() {
        Long id = 2L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.getStudentById(id);

        assertNotNull(result);
        verify(studentRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el estudiante no existe al obtener por ID")
    void getStudentById_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    @DisplayName("Debe eliminar un estudiante y sus entidades relacionadas")
    void deleteStudent_success() {
        Long id = 3L;
        User user = new User();
        person.setUser(user);
        student.setPerson(person);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        studentService.deleteStudent(id);

        verify(studentRepository).delete(student);
        verify(userRepository).delete(user);
        verify(personMapper, never()).fromCreateDto(any()); // No mapea persona en borrado
    }

    @Test
    @DisplayName("Debe lanzar excepción si intenta eliminar estudiante inexistente")
    void deleteStudent_notFound() {
        when(studentRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> studentService.deleteStudent(7L));
    }

    @Test
    @DisplayName("Debe asignar representantes a un estudiante existente")
    void assignRepresentatives_success() {
        Long id = 4L;
        student.setRepresentatives(new HashSet<>());
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(representativeRepository.findAllById(dto.getRepresentativeIds()))
                .thenReturn(List.of(new Representative()));

        studentService.assignRepresentativesToStudent(id, dto.getRepresentativeIds());

        verify(studentRepository).save(student);
        assertFalse(student.getRepresentatives().isEmpty());
    }

    @Test
    @DisplayName("Debe remover representantes de un estudiante existente")
    void removeRepresentatives_success() {
        Long id = 5L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        studentService.removeRepresentativesFromStudent(id, Set.of(1L,2L));

        verify(studentRepresentativeRepository).removeRepresentativesFromStudent(id, Set.of(1L,2L));
    }

    @Test
    @DisplayName("Debe obtener lista de representantes básicos por estudiante")
    void getRepresentativesByStudentId_success() {
        Long id = 6L;

        // Configuramos el mock de proyección
        when(projection.getId()).thenReturn(10L);
        when(projection.getFullName()).thenReturn("Ana María");

        when(studentRepository.findRepresentativesByStudentId(id))
                .thenReturn(List.of(projection));

        List<RepresentativeBasicDTO> result = studentService.getRepresentativesByStudentId(id);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals("Ana María", result.get(0).getFullName());
    }
}