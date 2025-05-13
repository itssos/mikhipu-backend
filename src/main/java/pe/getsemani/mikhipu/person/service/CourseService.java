package pe.getsemani.mikhipu.person.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.dto.create.CourseCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.CourseResponseDTO;
import pe.getsemani.mikhipu.person.entity.Course;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.entity.Teacher;
import pe.getsemani.mikhipu.person.mapper.CourseMapper;
import pe.getsemani.mikhipu.person.repository.CourseRepository;
import pe.getsemani.mikhipu.person.repository.StudentRepository;
import pe.getsemani.mikhipu.person.repository.TeacherRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper courseMapper;

    public CourseResponseDTO create(CourseCreateDTO dto) {
        Course course = courseMapper.toEntity(dto);
        return courseMapper.toDto(courseRepository.save(course));
    }


    public List<CourseResponseDTO> findAll() {
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
                .orElseThrow(() -> new IllegalArgumentException("Docente principal no encontrado: " + mainTeacherCode));

        Set<Teacher> auxiliaryTeachers = (auxiliaryTeacherCodes != null && !auxiliaryTeacherCodes.isEmpty())
                ? new HashSet<>(teacherRepository.findAllByCodeIn(auxiliaryTeacherCodes))
                : new HashSet<>();

        auxiliaryTeachers.removeIf(t -> t.getCode().equals(mainTeacherCode));

        course.getTeachers().clear();
        course.getTeachers().addAll(auxiliaryTeachers);

        course.setMainTeacher(mainTeacher);
        courseRepository.save(course);
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