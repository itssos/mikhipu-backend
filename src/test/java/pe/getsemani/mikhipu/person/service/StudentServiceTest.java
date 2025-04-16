package pe.getsemani.mikhipu.person.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.repository.StudentRepository;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de estudiantes")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    // Método auxiliar para crear un estudiante dummy
    private Student createDummyStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Juan");
        student.setLastName("Perez");
        student.setDni("12345678");
        student.setBirthDate(LocalDate.of(2005, 5, 1));
        student.setGender("M");
        student.setAddress("Calle 123");
        student.setPhone("987654321");
        student.setGrade(3);
        student.setSection(Section.A);
        student.setSchoolLevel(SchoolLevel.PRIMARIA);
        return student;
    }

    @Test
    @DisplayName("Debe listar todos los estudiantes")
    void listAllStudents_returnsListOfStudents() {
        Student student1 = createDummyStudent();
        Student student2 = createDummyStudent();
        student2.setId(2L);
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        var result = studentService.listAllStudents();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe obtener un estudiante por ID")
    void getStudentById_returnsStudent() {
        Student student = createDummyStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        var result = studentService.getStudentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe lanzar excepción si no se encuentra el estudiante por ID")
    void getStudentById_throwsException_whenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró al estudiante con ID:");
    }

    @Test
    @DisplayName("Debe crear un estudiante asignando usuario y rol")
    void createStudent_createsStudent() {
        Student student = createDummyStudent();
        student.setUser(null);

        // Simular codificación de contraseña
        when(passwordEncoder.encode(student.getDni())).thenReturn("encodedPassword");
        // Simular la búsqueda del rol de ESTUDIANTE
        Role role = new Role();
        role.setId(1);
        role.setName("ESTUDIANTE");
        when(roleRepository.findByName("ESTUDIANTE")).thenReturn(Optional.of(role));
        // Simular la persistencia del estudiante
        when(studentRepository.save(student)).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Student createdStudent = studentService.createStudent(student);

        assertThat(createdStudent.getId()).isNotNull();
        assertThat(createdStudent.getUser()).isNotNull();
        assertThat(createdStudent.getUser().getUsername()).isEqualTo(student.getDni());
        verify(passwordEncoder).encode(student.getDni());
    }

    @Test
    @DisplayName("Debe actualizar un estudiante existente")
    void updateStudent_updatesStudent() {
        Student student = createDummyStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Student updatedDetails = createDummyStudent();
        updatedDetails.setFirstName("Actualizado");

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student updatedStudent = studentService.updateStudent(1L, updatedDetails);

        assertThat(updatedStudent.getFirstName()).isEqualTo("Actualizado");
    }

    @Test
    @DisplayName("Debe eliminar un estudiante")
    void deleteStudent_deletesStudent() {
        Student student = createDummyStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(studentRepository).delete(student);
    }
}
