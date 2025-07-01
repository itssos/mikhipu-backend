package pe.getsemani.mikhipu.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatPermissionService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    /**
     * Permite chat directo solo si uno es profesor, el otro alumno y ambos comparten al menos un curso.
     */
    public boolean canDirectChat(String username, int toUserId) {
        System.out.println("[INFO] Iniciando canDirectChat: username=" + username + ", toUserId=" + toUserId);

        User from = userRepository.findByUsername(username).orElseThrow(() -> {
            System.out.println("[ERROR] Usuario origen no encontrado: " + username);
            return new NoSuchElementException("Usuario no encontrado: " + username);
        });

        User to = userRepository.findById(toUserId).orElseThrow(() -> {
            System.out.println("[ERROR] Usuario destino no encontrado: ID=" + toUserId);
            return new NoSuchElementException("Usuario no encontrado: ID=" + toUserId);
        });

        System.out.println("[DEBUG] Usuario origen: " + from.getUsername() + " (ID " + from.getId() + ")");
        System.out.println("[DEBUG] Usuario destino: " + to.getUsername() + " (ID " + to.getId() + ")");

        Optional<Student> fromStudentOpt = studentRepository.findByPerson_User_Username(from.getUsername());
        Optional<Student> toStudentOpt = studentRepository.findByPerson_User_Username(to.getUsername());

        Optional<Teacher> fromTeacherOpt = teacherRepository.findByPerson_User_Username(from.getUsername());
        Optional<Teacher> toTeacherOpt = teacherRepository.findByPerson_User_Username(to.getUsername());

        boolean fromIsTeacher = fromTeacherOpt.isPresent();
        boolean toIsTeacher = toTeacherOpt.isPresent();
        boolean fromIsStudent = fromStudentOpt.isPresent();
        boolean toIsStudent = toStudentOpt.isPresent();

        System.out.println("[DEBUG] Roles: fromIsTeacher=" + fromIsTeacher + ", fromIsStudent=" + fromIsStudent +
                ", toIsTeacher=" + toIsTeacher + ", toIsStudent=" + toIsStudent);

        // Solo se permite chat profe <-> alumno (no profe-profe ni alumno-alumno)
        if (fromIsTeacher == toIsTeacher) {
            System.out.println("[WARN] Ambos usuarios son del mismo tipo (profesor-profesor o alumno-alumno). Chat no permitido.");
            return false;
        }

        List<Course> fromCourses = getCoursesOfUser(from);
        List<Course> toCourses = getCoursesOfUser(to);

        System.out.println("[DEBUG] Cursos usuario origen: " + fromCourses.stream().map(Course::getId).toList());
        System.out.println("[DEBUG] Cursos usuario destino: " + toCourses.stream().map(Course::getId).toList());

        Set<Long> toCoursesIds = toCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

        boolean comparteCurso = fromCourses.stream()
                .map(Course::getId)
                .anyMatch(toCoursesIds::contains);

        System.out.println("[DEBUG] ¿Comparten curso? " + (comparteCurso ? "Sí" : "No"));

        if (!comparteCurso) {
            System.out.println("[WARN] Los usuarios no comparten ningún curso. Chat no permitido.");
            return false;
        }

        System.out.println("[INFO] Permiso concedido para chat directo entre " + from.getUsername() + " y " + to.getUsername());
        return true;
    }

    /**
     * Verifica si el usuario (profesor o alumno) está inscrito en el curso.
     */
    public boolean isEnrolledInCourse(String username, Long courseId) {
        System.out.println("[INFO] Verificando inscripción del usuario '" + username + "' en el curso ID " + courseId);

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            System.out.println("[WARN] Usuario '" + username + "' no encontrado.");
            return new NoSuchElementException("Usuario no encontrado: " + username);
        });

        List<Course> courses = getCoursesOfUser(user);

        boolean isEnrolled = courses.stream().anyMatch(c -> c.getId().equals(courseId));

        if (isEnrolled) {
            System.out.println("[INFO] Usuario '" + username + "' SÍ está inscrito en el curso ID " + courseId);
        } else {
            System.out.println("[INFO] Usuario '" + username + "' NO está inscrito en el curso ID " + courseId);
        }
        return isEnrolled;
    }

    /**
     * Obtiene todos los cursos donde el usuario participa como profesor o estudiante.
     */
    private List<Course> getCoursesOfUser(User user) {
        System.out.println("[DEBUG] Buscando cursos del usuario '" + user.getUsername() + "'");

        Optional<Student> studentOpt = studentRepository.findByPerson_User_Username(user.getUsername());
        if (studentOpt.isPresent()) {
            Long studentId = studentOpt.get().getId();
            System.out.println("[DEBUG] Usuario '" + user.getUsername() + "' identificado como ESTUDIANTE con ID " + studentId);
            List<Course> studentCourses = courseRepository.findByStudents_Id(studentId);
            System.out.println("[DEBUG] Cursos encontrados como estudiante: " + studentCourses.size());
            return studentCourses;
        }

        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(user.getUsername());
        if (teacherOpt.isPresent()) {
            Long teacherId = teacherOpt.get().getId();
            System.out.println("[DEBUG] Usuario '" + user.getUsername() + "' identificado como PROFESOR con ID " + teacherId);
            List<Course> teacherCourses = courseRepository.findByMainTeacher_Id(teacherId);
            System.out.println("[DEBUG] Cursos encontrados como profesor: " + teacherCourses.size());
            return teacherCourses;
        }

        System.out.println("[WARN] Usuario '" + user.getUsername() + "' no está registrado ni como estudiante ni como profesor.");
        return Collections.emptyList();
    }

}
