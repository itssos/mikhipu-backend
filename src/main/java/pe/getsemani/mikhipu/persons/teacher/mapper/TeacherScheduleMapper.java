package pe.getsemani.mikhipu.persons.teacher.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCourseScheduleResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.entity.TeacherCourseSchedule;

@Component
public class TeacherScheduleMapper {

    public TeacherCourseScheduleResponseDTO toDto(TeacherCourseSchedule schedule) {
        TeacherCourseScheduleResponseDTO dto = new TeacherCourseScheduleResponseDTO();
        dto.setId(schedule.getId());
        dto.setTeacherId(schedule.getTeacher().getId());
        dto.setTeacherFullName(schedule.getTeacher().getPerson().getFirstName()
                + " " + schedule.getTeacher().getPerson().getLastName());
        dto.setCourseId(schedule.getCourse().getId());
        dto.setCourseName(schedule.getCourse().getName());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        return dto;
    }
}