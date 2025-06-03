package pe.getsemani.mikhipu.persons.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.getsemani.mikhipu.course.dto.ManageRepresentativesDTO;
import pe.getsemani.mikhipu.course.dto.UploadResponse;
import pe.getsemani.mikhipu.persons.representative.dto.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentCreateDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentCourseViewDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentFilterDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.student.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Obtiene lista paginada y filtrada de estudiantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado de estudiantes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('GET_STUDENTS')")
    public ResponseEntity<Page<StudentCourseViewDTO>> getAllStudents(
            @ParameterObject StudentFilterDTO filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(studentService.getAllStudentsFiltered(filter, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_STUDENT')")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_STUDENT')")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentCreateDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_STUDENT')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/list")
    @PreAuthorize("hasAuthority('UPLOAD_STUDENT_LIST')")
    public ResponseEntity<UploadResponse> uploadStudentList(@Valid @RequestBody List<StudentCreateDTO> students) {
        UploadResponse response = studentService.uploadStudentsFromList(students);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/excel")
    @PreAuthorize("hasAuthority('UPLOAD_STUDENT_EXCEL')")
    public ResponseEntity<UploadResponse> uploadStudentsFromExcel(@RequestParam("file") MultipartFile file) {
        UploadResponse response = studentService.uploadStudentsFromExcel(file);
        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("hasAuthority('ASSIGN_REPRESENTATIVES')")
    @PostMapping("/{studentId}/representatives")
    public ResponseEntity<Void> assignRepresentatives(
            @PathVariable Long studentId,
            @Valid @RequestBody ManageRepresentativesDTO dto
    ) {
        studentService.assignRepresentativesToStudent(studentId, dto.getRepresentativeIds());
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('REMOVE_REPRESENTATIVES')")
    @DeleteMapping("/{studentId}/representatives")
    public ResponseEntity<Void> removeRepresentatives(
            @PathVariable Long studentId,
            @Valid @RequestBody ManageRepresentativesDTO dto
    ) {
        studentService.removeRepresentativesFromStudent(studentId, dto.getRepresentativeIds());
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('GET_STUDENT_REPRESENTATIVES')")
    @GetMapping("/{studentId}/representatives")
    public ResponseEntity<List<RepresentativeBasicDTO>> getRepresentativesOfStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getRepresentativesByStudentId(studentId));
    }

}
