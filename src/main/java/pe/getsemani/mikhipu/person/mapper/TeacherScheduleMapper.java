package pe.getsemani.mikhipu.person.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.person.dto.response.TeacherCourseScheduleResponseDTO;
import pe.getsemani.mikhipu.person.entity.TeacherCourseSchedule;

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