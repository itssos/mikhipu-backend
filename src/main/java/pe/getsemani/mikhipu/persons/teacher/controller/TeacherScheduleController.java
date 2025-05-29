package pe.getsemani.mikhipu.persons.teacher.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.course.dto.TeacherScheduleDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCourseScheduleResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.service.TeacherScheduleService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/teacher-schedules")
@RequiredArgsConstructor
public class TeacherScheduleController {

    private final TeacherScheduleService scheduleService;

//    @PreAuthorize("hasAuthority('VIEW_SCHEDULES')")
    @GetMapping
    public ResponseEntity<List<TeacherCourseScheduleResponseDTO>> getSchedules(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) DayOfWeek dayOfWeek
    ) {
        List<TeacherCourseScheduleResponseDTO> result =
                scheduleService.filterSchedules(teacherId, courseId, dayOfWeek);
        return ResponseEntity.ok(result);
    }

//    @PreAuthorize("hasAuthority('ASSIGN_SCHEDULE')")
    @PostMapping
    public ResponseEntity<Void> assignSchedule(@Valid @RequestBody TeacherScheduleDTO dto) {
        scheduleService.assignOrUpdateSchedule(dto);
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('REMOVE_SCHEDULE')")
    @DeleteMapping
    public ResponseEntity<Void> deleteSchedule(
            @RequestParam Long teacherId,
            @RequestParam Long courseId,
            @RequestParam DayOfWeek dayOfWeek
    ) {
        scheduleService.deleteSchedule(teacherId, courseId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }
}
