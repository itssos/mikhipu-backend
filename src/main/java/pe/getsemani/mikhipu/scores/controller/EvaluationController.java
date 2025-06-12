package pe.getsemani.mikhipu.scores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.scores.dto.EvaluationCreateDTO;
import pe.getsemani.mikhipu.scores.dto.EvaluationFilterDTO;
import pe.getsemani.mikhipu.scores.dto.EvaluationResponseDTO;
import pe.getsemani.mikhipu.scores.service.EvaluationService;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluations", description = "Gestión de evaluaciones")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final TeacherRepository teacherRepository;

    @PreAuthorize("hasAuthority('CREATE_EVALUATION')")
    @PostMapping
    @Operation(summary = "Crear evaluación", description = "Solo docentes pueden crear evaluaciones para un curso")
    public ResponseEntity<EvaluationResponseDTO> create(
            @Valid @RequestBody EvaluationCreateDTO dto
    ) {
        return ResponseEntity.ok(evaluationService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_EVALUATION')")
    @Operation(summary = "Editar evaluación", description = "Edita una evaluación existente (solo docentes dueños o admin)")
    public ResponseEntity<EvaluationResponseDTO> edit(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCreateDTO dto
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(evaluationService.edit(id, dto, username));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_EVALUATION')")
    @Operation(summary = "Eliminar evaluación", description = "Elimina una evaluación (y notas asociadas). Solo docentes dueños o admin.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        evaluationService.delete(id, username);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('GET_EVALUATION')")
    @PostMapping("/filter")
    @Operation(
            summary = "Listar evaluaciones con filtros y paginación",
            description = "Permite filtrar por curso, docente, año, trimestre, tipo y rango de fechas. Si el usuario es docente, solo ve sus evaluaciones. Si es admin, puede filtrar por cualquier docente."
    )
    public ResponseEntity<Page<EvaluationResponseDTO>> filterEvaluations(
            @Valid @RequestBody EvaluationFilterDTO filter,
            @ParameterObject Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Page<EvaluationResponseDTO> result = evaluationService.filterEvaluations(filter, pageable, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar detalle de evaluación",
            description = "Obtiene los detalles de una evaluación por su ID"
    )
    public ResponseEntity<EvaluationResponseDTO> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getDetails(id));
    }
}
