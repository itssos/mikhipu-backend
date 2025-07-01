package pe.getsemani.mikhipu.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test de ChatPermissionService")
class ChatPermissionServiceTest {

    @Mock UserRepository userRepository;
    @Mock StudentRepository studentRepository;
    @Mock TeacherRepository teacherRepository;
    @Mock CourseRepository courseRepository;

    @InjectMocks ChatPermissionService chatPermissionService;

    User profe, alumno, otroProfe, otroAlumno;
    Teacher teacher;
    Student student;
    Course course1, course2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Datos comunes
        profe = new User(); profe.setId(1); profe.setUsername("profe");
        alumno = new User(); alumno.setId(2); alumno.setUsername("alumno");
        otroProfe = new User(); otroProfe.setId(3); otroProfe.setUsername("otroProfe");
        otroAlumno = new User(); otroAlumno.setId(4); otroAlumno.setUsername("otroAlumno");

        teacher = new Teacher(); teacher.setId(100L);
        student = new Student(); student.setId(200L);

        course1 = new Course(); course1.setId(10L); course2 = new Course(); course2.setId(20L);
    }

    // ------------- CAJA NEGRA --------------

    @Nested
    @DisplayName("Caja negra: canDirectChat()")
    class BlackBoxCanDirectChat {

        @Test
        @DisplayName("Permite chat solo entre profesor y alumno que comparten curso")
        void testProfeAlumnoCompartenCurso() {
            // Mocks de usuarios y roles
            when(userRepository.findByUsername("profe")).thenReturn(Optional.of(profe));
            when(userRepository.findById(2)).thenReturn(Optional.of(alumno));
            when(teacherRepository.findByPerson_User_Username("profe")).thenReturn(Optional.of(teacher));
            when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
            // Ambos comparten curso1
            when(courseRepository.findByMainTeacher_Id(teacher.getId())).thenReturn(List.of(course1));
            when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course1));

            assertTrue(chatPermissionService.canDirectChat("profe", 2));
        }

        @Test
        @DisplayName("No permite chat si ambos son profesores")
        void testAmbosProfesores() {
            when(userRepository.findByUsername("profe")).thenReturn(Optional.of(profe));
            when(userRepository.findById(3)).thenReturn(Optional.of(otroProfe));
            when(teacherRepository.findByPerson_User_Username("profe")).thenReturn(Optional.of(teacher));
            when(teacherRepository.findByPerson_User_Username("otroProfe")).thenReturn(Optional.of(new Teacher()));
            when(studentRepository.findByPerson_User_Username(anyString())).thenReturn(Optional.empty());
            assertFalse(chatPermissionService.canDirectChat("profe", 3));
        }

        @Test
        @DisplayName("No permite chat si ambos son alumnos")
        void testAmbosAlumnos() {
            when(userRepository.findByUsername("alumno")).thenReturn(Optional.of(alumno));
            when(userRepository.findById(4)).thenReturn(Optional.of(otroAlumno));
            when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
            when(studentRepository.findByPerson_User_Username("otroAlumno")).thenReturn(Optional.of(new Student()));
            when(teacherRepository.findByPerson_User_Username(anyString())).thenReturn(Optional.empty());
            assertFalse(chatPermissionService.canDirectChat("alumno", 4));
        }

        @Test
        @DisplayName("No permite chat si no comparten curso")
        void testNoCompartenCurso() {
            when(userRepository.findByUsername("profe")).thenReturn(Optional.of(profe));
            when(userRepository.findById(2)).thenReturn(Optional.of(alumno));
            when(teacherRepository.findByPerson_User_Username("profe")).thenReturn(Optional.of(teacher));
            when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
            when(courseRepository.findByMainTeacher_Id(teacher.getId())).thenReturn(List.of(course1));
            when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course2)); // diferente
            assertFalse(chatPermissionService.canDirectChat("profe", 2));
        }

        @Test
        @DisplayName("Lanza excepción si el usuario origen no existe")
        void testUsuarioOrigenNoExiste() {
            when(userRepository.findByUsername("inexistente")).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> chatPermissionService.canDirectChat("inexistente", 2));
        }

        @Test
        @DisplayName("Lanza excepción si el usuario destino no existe")
        void testUsuarioDestinoNoExiste() {
            when(userRepository.findByUsername("profe")).thenReturn(Optional.of(profe));
            when(userRepository.findById(99)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> chatPermissionService.canDirectChat("profe", 99));
        }
    }

    // ------------- CAJA BLANCA (Cobertura caminos y mocks) -------------

    @Nested
    @DisplayName("Caja blanca: isEnrolledInCourse() y lógica interna")
    class WhiteBoxEnrolledAndCourses {

        @Test
        @DisplayName("isEnrolledInCourse retorna true si está inscrito")
        void testInscritoEnCurso() {
            when(userRepository.findByUsername("alumno")).thenReturn(Optional.of(alumno));
            when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
            when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course1, course2));

            assertTrue(chatPermissionService.isEnrolledInCourse("alumno", 10L));
            assertTrue(chatPermissionService.isEnrolledInCourse("alumno", 20L));
        }

        @Test
        @DisplayName("isEnrolledInCourse retorna false si NO está inscrito")
        void testNoInscritoEnCurso() {
            when(userRepository.findByUsername("alumno")).thenReturn(Optional.of(alumno));
            when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
            when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course1));

            assertFalse(chatPermissionService.isEnrolledInCourse("alumno", 20L));
        }

        @Test
        @DisplayName("Si el usuario no es ni estudiante ni profesor, no retorna cursos")
        void testNoEsNiAlumnoNiProfe() {
            when(userRepository.findByUsername("sinrol")).thenReturn(Optional.of(new User() {{ setUsername("sinrol"); }}));
            when(studentRepository.findByPerson_User_Username("sinrol")).thenReturn(Optional.empty());
            when(teacherRepository.findByPerson_User_Username("sinrol")).thenReturn(Optional.empty());
            // isEnrolledInCourse usa getCoursesOfUser
            assertFalse(chatPermissionService.isEnrolledInCourse("sinrol", 10L));
        }
    }

    // ------------- TEST UNITARIOS -------------

    @Test
    @DisplayName("Unitario: getCoursesOfUser retorna cursos como estudiante")
    void testGetCoursesOfUserAsStudent() throws Exception {
        when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
        when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course1, course2));
        // forzar acceso por reflexión (private method)
        var method = ChatPermissionService.class.getDeclaredMethod("getCoursesOfUser", User.class);
        method.setAccessible(true);
        List<Course> result = (List<Course>) method.invoke(chatPermissionService, alumno);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Unitario: getCoursesOfUser retorna cursos como profesor")
    void testGetCoursesOfUserAsTeacher() throws Exception {
        when(studentRepository.findByPerson_User_Username("profe")).thenReturn(Optional.empty());
        when(teacherRepository.findByPerson_User_Username("profe")).thenReturn(Optional.of(teacher));
        when(courseRepository.findByMainTeacher_Id(teacher.getId())).thenReturn(List.of(course1));
        var method = ChatPermissionService.class.getDeclaredMethod("getCoursesOfUser", User.class);
        method.setAccessible(true);
        List<Course> result = (List<Course>) method.invoke(chatPermissionService, profe);
        assertEquals(1, result.size());
    }

    // ------------- TDD -------------

    // Ya lo implementamos
    @Test
    @DisplayName("TDD: Permitir chat solo entre profesor y alumno que comparten al menos un curso")
    void tddPermiteChatProfeAlumnoSiCompartenCurso() {
        // Arrange: Mocks necesarios
        when(userRepository.findByUsername("profe")).thenReturn(Optional.of(profe));
        when(userRepository.findById(2)).thenReturn(Optional.of(alumno));
        when(teacherRepository.findByPerson_User_Username("profe")).thenReturn(Optional.of(teacher));
        when(studentRepository.findByPerson_User_Username("alumno")).thenReturn(Optional.of(student));
        when(courseRepository.findByMainTeacher_Id(teacher.getId())).thenReturn(List.of(course1));
        when(courseRepository.findByStudents_Id(student.getId())).thenReturn(List.of(course1));

        // Act & Assert: El chat está permitido
        assertTrue(chatPermissionService.canDirectChat("profe", 2),
                "Debería permitir chat entre profesor y alumno si comparten al menos un curso");
    }

}
