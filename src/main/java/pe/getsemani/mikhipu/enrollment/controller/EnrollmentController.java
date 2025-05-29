package pe.getsemani.mikhipu.enrollment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.course.dto.UpdateEnrollmentStatusDTO;
import pe.getsemani.mikhipu.enrollment.dto.EnrollmentCreateDTO;
import pe.getsemani.mikhipu.enrollment.dto.EnrollmentResponseDTO;
import pe.getsemani.mikhipu.enrollment.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

//    @PreAuthorize("hasAuthority('CREATE_ENROLLMENT')")
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(
            @Valid @RequestBody EnrollmentCreateDTO dto) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(dto));
    }

//    @PreAuthorize("hasAuthority('UPDATE_ENROLLMENT')")
    @PutMapping("/status")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollmentStatus(
            @Valid @RequestBody UpdateEnrollmentStatusDTO dto) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(dto));
    }

//    @PreAuthorize("hasAuthority('GET_ENROLLMENTS')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.findAllByStudentId(studentId));
    }
}