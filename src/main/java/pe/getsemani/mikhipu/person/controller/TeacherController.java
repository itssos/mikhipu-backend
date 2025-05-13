package pe.getsemani.mikhipu.person.controller;

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
import pe.getsemani.mikhipu.person.dto.create.TeacherCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.dto.response.TeacherResponseDTO;
import pe.getsemani.mikhipu.person.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

//    @PreAuthorize("hasAuthority('CREATE_TEACHER')")
    @PostMapping
    public ResponseEntity<TeacherResponseDTO> create(@RequestBody TeacherCreateDTO dto) {
        return ResponseEntity.ok(teacherService.create(dto));
    }

//    @PreAuthorize("hasAuthority('GET_TEACHERS')")
    @GetMapping
    public ResponseEntity<List<TeacherResponseDTO>> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

//    @PreAuthorize("hasAuthority('GET_TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

//    @PreAuthorize("hasAuthority('UPDATE_TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> update(@PathVariable Long id, @RequestBody TeacherCreateDTO dto) {
        return ResponseEntity.ok(teacherService.update(id, dto));
    }

//    @PreAuthorize("hasAuthority('DELETE_TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
//    @PreAuthorize("hasAuthority('GET_TEACHER_STUDENTS')")
    @GetMapping("/{teacherId}/students")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getStudentsTaughtByTeacher(teacherId));
    }
}