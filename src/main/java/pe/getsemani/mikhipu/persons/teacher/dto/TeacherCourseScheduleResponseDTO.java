package pe.getsemani.mikhipu.persons.teacher.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class TeacherCourseScheduleResponseDTO {
    private Long id;
    private Long teacherId;
    private String teacherFullName;
    private Long courseId;
    private String courseName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
}