package pe.getsemani.mikhipu.course.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.course.dto.create.CourseCreateDTO;
import pe.getsemani.mikhipu.course.dto.response.CourseResponseDTO;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.persons.teacher.mapper.TeacherMapper;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    public Course toEntity(CourseCreateDTO dto) {
        return Course.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .year(dto.getYear())
                .quarter(dto.getQuarter())
                .build();
    }

    public CourseResponseDTO toDto(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setCode(course.getCode());
        dto.setDescription(course.getDescription());
        dto.setYear(course.getYear());
        dto.setQuarter(course.getQuarter());
//        dto.setMainTeacher(course.getMainTeacher() != null ? teacherMapper.toDto(course.getMainTeacher()) : null);
//        dto.setTeachers(course.getTeachers().stream()
//                .map(teacherMapper::toDto)
//                .collect(Collectors.toSet()));
//        dto.setStudents(course.getStudents().stream()
//                .map(studentMapper::toDto)
//                .collect(Collectors.toSet()));
        return dto;
    }
}