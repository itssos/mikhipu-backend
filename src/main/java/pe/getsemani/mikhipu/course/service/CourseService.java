package pe.getsemani.mikhipu.course.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.course.dto.CourseTeacherViewDTO;
import pe.getsemani.mikhipu.course.dto.create.CourseCreateDTO;
import pe.getsemani.mikhipu.course.dto.response.CourseResponseDTO;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.dto.StudentCourseViewDTO;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;
import pe.getsemani.mikhipu.course.mapper.CourseMapper;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.course.repository.CourseRelationRepository;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.course.repository.CourseStudentRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper courseMapper;
    private final CourseStudentRepository courseStudentRepository;
    private final StudentMapper studentMapper;
    private final CourseRelationRepository courseRelationRepository;
    private final RepresentativeRepository representativeRepository;

    public CourseResponseDTO create(CourseCreateDTO dto) {
        Course course = courseMapper.toEntity(dto);
        return courseMapper.toDto(courseRepository.save(course));
    }


    public List<CourseResponseDTO> findAll(String username) {
        // 1. Si es docente: sus cursos (mainTeacher o teachers)
        Optional<Teacher> teacherOpt = teacherRepository.findByPerson_User_Username(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Set<Course> courses = new HashSet<>();
            // MainTeacher
            courses.addAll(courseRepository.findByMainTeacher_Id(teacher.getId()));
            // Docente colaborador
            courses.addAll(courseRepository.findByTeachers_Id(teacher.getId()));
            return courses.stream()
                    .map(courseMapper::toDto)
                    .collect(Collectors.toList());
        }

        // 2. Si es estudiante: cursos donde es estudiante
        Optional<Student> studentOpt = studentRepository.findByPerson_User_Username(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<Course> courses = courseRepository.findByStudents_Id(student.getId());
            return courses.stream()
                    .map(courseMapper::toDto)
                    .collect(Collectors.toList());
        }

        // 3. Si es apoderado: cursos de los hijos
        Optional<Representative> repOpt = representativeRepository.findByPerson_User_Username(username);
        if (repOpt.isPresent()) {
            Representative rep = repOpt.get();
            Set<Course> courses = new HashSet<>();
            for (Student s : rep.getStudents()) {
                courses.addAll(courseRepository.findByStudents_Id(s.getId()));
            }
            return courses.stream()
                    .map(courseMapper::toDto)
                    .collect(Collectors.toList());
        }

        // 4. Admin u otros: todos los cursos
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO findById(Long id) {
        return courseRepository.findById(id)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
    }

    public CourseResponseDTO update(Long id, CourseCreateDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        course.setName(dto.getName());
        course.setCode(dto.getCode());
        course.setDescription(dto.getDescription());
        course.setYear(dto.getYear());
        course.setQuarter(dto.getQuarter());

        return courseMapper.toDto(courseRepository.save(course));
    }

    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STR."Curso no encontrado con ID: \{id}"));

        courseRepository.delete(course);
    }

    @Transactional
    public void assignTeachersToCourse(Long courseId, String mainTeacherCode, Set<String> auxiliaryTeacherCodes) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        Teacher mainTeacher = teacherRepository.findByCode(mainTeacherCode)
                .orElseThrow(() -> new IllegalArgumentException("Docente principal no encontrado"));

        Set<Teacher> auxiliaries = auxiliaryTeacherCodes != null
                ? new HashSet<>(teacherRepository.findAllByCodeIn(auxiliaryTeacherCodes))
                : Set.of();

        auxiliaries.removeIf(t -> t.getCode().equals(mainTeacherCode));

        course.setMainTeacher(mainTeacher);
        courseRepository.save(course);

        courseRelationRepository.assignAuxiliaryTeachers(courseId,
                auxiliaries.stream().map(Teacher::getId).collect(Collectors.toSet()));
    }

    @Transactional
    public void removeTeachersFromCourse(Long courseId, Set<String> teacherCodes) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        Teacher main = course.getMainTeacher();
        if (main != null && teacherCodes.contains(main.getCode())) {
            course.setMainTeacher(null);
            courseRepository.save(course);
        }

        courseRelationRepository.removeTeachersByCode(courseId, teacherCodes);
    }

    @Transactional
    public void assignStudentsToCourse(Long courseId, Set<Long> studentIds) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        courseRelationRepository.assignStudents(courseId, studentIds);
    }

    @Transactional
    public void removeStudentsFromCourse(Long courseId, Set<Long> studentIds) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        courseRelationRepository.removeStudents(courseId, studentIds);
    }

    public List<CourseTeacherViewDTO> getTeachersByCourseId(Long courseId) {
        return courseRepository.findTeachersByCourseId(courseId).stream()
                .map(proj -> {
                    CourseTeacherViewDTO dto = new CourseTeacherViewDTO();
                    dto.setId(proj.getId());
                    dto.setFullName(proj.getFullName());
                    dto.setCode(proj.getCode());
                    dto.setRole(proj.getRole());
                    return dto;
                })
                .toList();
    }


    public List<StudentCourseViewDTO> findStudentsByCourseIdLight(Long courseId) {
        return courseRepository.findStudentCourseViewByCourseId(courseId).stream()
                .map(proj -> {
                    StudentCourseViewDTO dto = new StudentCourseViewDTO();
                    dto.setId(proj.getId());
                    dto.setFullName(proj.getFullName());
                    dto.setDni(proj.getDni());
                    dto.setGrade(proj.getGrade());
                    dto.setSection(Section.valueOf(proj.getSection()));
                    dto.setSchoolLevel(SchoolLevel.valueOf(proj.getSchoolLevel()));
                    return dto;
                })
                .toList();
    }

    private Teacher findTeacherByCode(String code) {
        return (Teacher) teacherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(STR."Docente con código \{code} no encontrado"));
    }

    private Student findStudentByDni(String dni) {
        return (Student) studentRepository.findByPersonDni(dni)
                .orElseThrow(() -> new IllegalArgumentException(STR."Estudiante con DNI \{dni} no encontrado"));
    }

}