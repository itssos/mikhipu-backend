package pe.getsemani.mikhipu.person.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.person.dto.TeacherScheduleDTO;
import pe.getsemani.mikhipu.person.dto.response.TeacherCourseScheduleResponseDTO;
import pe.getsemani.mikhipu.person.entity.Course;
import pe.getsemani.mikhipu.person.entity.TeacherCourseSchedule;
import pe.getsemani.mikhipu.person.mapper.TeacherScheduleMapper;
import pe.getsemani.mikhipu.person.repository.CourseRepository;
import pe.getsemani.mikhipu.person.repository.TeacherCourseScheduleRepository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherScheduleService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TeacherCourseScheduleRepository scheduleRepository;
    private final TeacherScheduleMapper mapper;

    public List<TeacherCourseScheduleResponseDTO> filterSchedules(
            Long teacherId, Long courseId, DayOfWeek dayOfWeek) {

        List<TeacherCourseSchedule> schedules;

        if (teacherId != null && courseId != null && dayOfWeek != null) {
            schedules = scheduleRepository.findByTeacherIdAndCourseIdAndDayOfWeek(teacherId, courseId, dayOfWeek)
                    .map(List::of)
                    .orElse(List.of());
        } else if (teacherId != null && courseId != null) {
            schedules = scheduleRepository.findByTeacherIdAndCourseId(teacherId, courseId);
        } else if (teacherId != null) {
            schedules = scheduleRepository.findByTeacherId(teacherId);
        } else if (courseId != null) {
            schedules = scheduleRepository.findByCourseId(courseId);
        } else {
            schedules = scheduleRepository.findAll();
        }

        return schedules.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void assignOrUpdateSchedule(TeacherScheduleDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        LocalTime time = LocalTime.parse(dto.getStartTime());

        TeacherCourseSchedule schedule = scheduleRepository
                .findByTeacherIdAndCourseIdAndDayOfWeek(dto.getTeacherId(), dto.getCourseId(), dto.getDayOfWeek())
                .orElse(TeacherCourseSchedule.builder()
                        .teacher(teacher)
                        .course(course)
                        .dayOfWeek(dto.getDayOfWeek())
                        .build());

        schedule.setStartTime(time);
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long teacherId, Long courseId, DayOfWeek dayOfWeek) {
        TeacherCourseSchedule schedule = scheduleRepository
                .findByTeacherIdAndCourseIdAndDayOfWeek(teacherId, courseId, dayOfWeek)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

        scheduleRepository.delete(schedule);
    }
}
