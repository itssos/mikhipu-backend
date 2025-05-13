package pe.getsemani.mikhipu.person.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.person.dto.UpdateEnrollmentStatusDTO;
import pe.getsemani.mikhipu.person.dto.create.EnrollmentCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.EnrollmentResponseDTO;
import pe.getsemani.mikhipu.person.service.EnrollmentService;

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
    @PatchMapping("/status")
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