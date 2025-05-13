package pe.getsemani.mikhipu.person.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.person.dto.AssignTeachersDTO;
import pe.getsemani.mikhipu.person.dto.CourseTeacherViewDTO;
import pe.getsemani.mikhipu.person.dto.ManageStudentsDTO;
import pe.getsemani.mikhipu.person.dto.RemoveTeachersDTO;
import pe.getsemani.mikhipu.person.dto.create.CourseCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.CourseResponseDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentCourseViewDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

//    @PreAuthorize("hasAuthority('CREATE_COURSE')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> create(@RequestBody CourseCreateDTO dto) {
        return ResponseEntity.ok(courseService.create(dto));
    }

//    @PreAuthorize("hasAuthority('GET_COURSES')")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

//    @PreAuthorize("hasAuthority('GET_COURSE')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

//    @PreAuthorize("hasAuthority('UPDATE_COURSE')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> update(@PathVariable Long id, @RequestBody CourseCreateDTO dto) {
        return ResponseEntity.ok(courseService.update(id, dto));
    }

//    @PreAuthorize("hasAuthority('DELETE_COURSE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('ASSIGN_TEACHERS')")
    @PostMapping("/{courseId}/assign-teachers")
    public ResponseEntity<CourseResponseDTO> assignTeachers(
            @PathVariable Long courseId,
            @Valid @RequestBody AssignTeachersDTO dto
    ) {
        courseService.assignTeachersToCourse(courseId, dto.getMainTeacherCode(), dto.getAuxiliaryTeacherCodes());
        return ResponseEntity.ok(courseService.findById(courseId));
    }

//    @PreAuthorize("hasAuthority('REMOVE_TEACHERS')")
    @DeleteMapping("/{courseId}/assign-teachers")
    public ResponseEntity<CourseResponseDTO> removeTeachers(
            @PathVariable Long courseId,
            @Valid @RequestBody RemoveTeachersDTO dto
    ) {
        courseService.removeTeachersFromCourse(courseId, dto.getTeacherCodes());
        return ResponseEntity.ok(courseService.findById(courseId));
    }

    // @PreAuthorize("hasAuthority('GET_COURSE_STUDENTS')")
    @GetMapping("/{courseId}/students/summary")
    public ResponseEntity<List<StudentCourseViewDTO>> getStudentsSummaryByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.findStudentsByCourseIdLight(courseId));
    }

    // @PreAuthorize("hasAuthority('GET_TEACHERS_OF_COURSE')")
    @GetMapping("/{courseId}/teachers")
    public ResponseEntity<List<CourseTeacherViewDTO>> getTeachersOfCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getTeachersByCourseId(courseId));
    }


    //    @PreAuthorize("hasAuthority('ASSIGN_STUDENTS')")
    @PostMapping("/{courseId}/students")
    public ResponseEntity<Void> assignStudents(
            @PathVariable Long courseId,
            @Valid @RequestBody ManageStudentsDTO dto
    ) {
        courseService.assignStudentsToCourse(courseId, dto.getStudentIds());
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('REMOVE_STUDENTS')")
    @DeleteMapping("/{courseId}/students")
    public ResponseEntity<Void> removeStudents(
            @PathVariable Long courseId,
            @Valid @RequestBody ManageStudentsDTO dto
    ) {
        courseService.removeStudentsFromCourse(courseId, dto.getStudentIds());
        return ResponseEntity.noContent().build();
    }
}