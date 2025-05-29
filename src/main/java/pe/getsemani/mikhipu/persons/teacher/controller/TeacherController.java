package pe.getsemani.mikhipu.persons.teacher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCreateDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Docentes", description = "Operaciones CRUD y de asignación relacionadas a docentes")
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "Crear un nuevo docente", description = "Registra un nuevo docente y su información personal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados", content = @Content)
    })
    @PreAuthorize("hasAuthority('CREATE_TEACHER')")
    @PostMapping
    public ResponseEntity<TeacherResponseDTO> create(@Valid @RequestBody TeacherCreateDTO dto) {
        return ResponseEntity.ok(teacherService.create(dto));
    }

    @Operation(summary = "Obtener todos los docentes", description = "Lista todos los docentes registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content)
    })
    @PreAuthorize("hasAuthority('GET_TEACHERS')")
    @GetMapping
    public ResponseEntity<List<TeacherResponseDTO>> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @Operation(summary = "Obtener docente por ID", description = "Busca un docente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado", content = @Content)
    })
    @PreAuthorize("hasAuthority('GET_TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @Operation(summary = "Actualizar docente", description = "Modifica los datos de un docente existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente actualizado correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado", content = @Content)
    })
    @PreAuthorize("hasAuthority('UPDATE_TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody TeacherCreateDTO dto) {
        return ResponseEntity.ok(teacherService.update(id, dto));
    }

    @Operation(summary = "Eliminar docente", description = "Elimina un docente, su persona y su usuario asociado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Docente eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado", content = @Content)
    })
    @PreAuthorize("hasAuthority('DELETE_TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar estudiantes por docente", description = "Retorna los estudiantes asignados a los cursos impartidos por un docente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('GET_TEACHER_STUDENTS')")
    @GetMapping("/{teacherId}/students")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getStudentsTaughtByTeacher(teacherId));
    }
}
