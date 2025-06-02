package pe.getsemani.mikhipu.assistance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionResponseDTO;
import pe.getsemani.mikhipu.assistance.service.AssistanceSessionService;

@Tag(name = "Configuración de Asistencia", description = "Endpoints para configurar reglas de asistencia globales")
@RestController
@RequestMapping("/api/assistance/config")
@RequiredArgsConstructor
public class AssistanceSessionController {

    private final AssistanceSessionService sessionService;

    @Operation(
            summary = "Crear configuración global de asistencia",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuración creada/actualizada exitosamente",
                            content = @Content(schema = @Schema(implementation = AssistanceSessionResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<AssistanceSessionResponseDTO> create(@Valid @RequestBody AssistanceSessionCreateDTO dto) {
        AssistanceSessionResponseDTO response = sessionService.saveSession(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar configuración global de asistencia",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuración actualizada",
                            content = @Content(schema = @Schema(implementation = AssistanceSessionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "No existe la configuración", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AssistanceSessionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AssistanceSessionCreateDTO dto) {
        AssistanceSessionResponseDTO response = sessionService.updateSession(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener configuración activa",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuración encontrada",
                            content = @Content(schema = @Schema(implementation = AssistanceSessionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "No existe configuración activa", content = @Content)
            }
    )
    @GetMapping("/active")
    public ResponseEntity<AssistanceSessionResponseDTO> getActive() {
        AssistanceSessionResponseDTO response = sessionService.getActiveSessions();
        return ResponseEntity.ok(response);
    }
}
