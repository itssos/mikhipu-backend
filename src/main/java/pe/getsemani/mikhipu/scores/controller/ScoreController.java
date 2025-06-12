package pe.getsemani.mikhipu.scores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.scores.dto.*;
import pe.getsemani.mikhipu.scores.service.ScoreService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
@Tag(name = "Scores", description = "Gestión de calificaciones y notas")
public class ScoreController {

    private final ScoreService scoreService;
    private final StudentRepository studentRepository;

    @PreAuthorize("hasAuthority('SCORE_REGISTER')")
    @PostMapping
    @Operation(
            summary = "Registrar nota",
            description = "Solo docentes del curso pueden registrar una nota"
    )
    public ResponseEntity<ScoreResponseDTO> registerScore(
            @Valid @RequestBody ScoreCreateDTO dto
    ) {
        return ResponseEntity.ok(scoreService.registerScore(dto));
    }

    @PreAuthorize("hasAuthority('SCORE_SELF_VIEW')")
    @PostMapping("/me")
    @Operation(
            summary = "Consultar mis notas",
            description = "Devuelve las notas del estudiante autenticado. Permite filtros avanzados por curso, evaluación, trimestre, año o rango de fechas."
    )
    public ResponseEntity<Page<ScoreResponseDTO>> getMyScores(
            @Valid @RequestBody ScoreFilterDTOMe filter,
            @ParameterObject Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentRepository.findByPerson_User_Username(username)
                .orElseThrow(() -> new RuntimeException("No es un estudiante"));

        Page<ScoreResponseDTO> page = scoreService.findScoresByStudentWithFilters(
                student.getId(), filter, pageable
        );
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasAuthority('SCORE_CHILDREN_VIEW')")
    @PostMapping("/my-children")
    @Operation(
            summary = "Consultar notas de mis hijos (paginado por hijo)",
            description = "Devuelve, paginado, las notas agrupadas por estudiante donde el usuario autenticado es representante."
    )
    public ResponseEntity<Page<RepresentativeScoreHistoryDTO>> getMyChildrenScores(
            @Valid @RequestBody ScoreFilterDTOMe filter,
            @ParameterObject Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Page<RepresentativeScoreHistoryDTO> result = scoreService.findScoresOfMyChildren(
                username, filter, pageable
        );
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('SCORE_HISTORY_VIEW')")
    @PostMapping("/history")
    @Operation(
            summary = "Consultar historial de notas (paginado)",
            description = "Devuelve el historial de notas filtrando por estudiante, curso, evaluación, año, trimestre o rango de fechas, con respuesta paginada."
    )
    public ResponseEntity<Page<ScoreResponseDTO>> getHistory(
            @Valid @RequestBody ScoreHistoryFilterDTO filter,
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(scoreService.getHistory(filter, pageable, username));
    }

    @PreAuthorize("hasAuthority('SCORE_AVERAGE_VIEW')")
    @GetMapping("/average")
    @Operation(
            summary = "Consultar promedio ponderado",
            description = "Calcula el promedio ponderado del estudiante en un curso y trimestre/año"
    )
    public ResponseEntity<Double> getWeightedAverage(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam String year,
            @RequestParam String quarter
    ) {
        return ResponseEntity.ok(
                scoreService.getWeightedAverage(studentId, courseId, year, quarter)
        );
    }

}
