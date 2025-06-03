package pe.getsemani.mikhipu.assistance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordUpdateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.service.AssistanceRecordService;

import java.util.List;

@Tag(name = "Registros de Asistencia", description = "Operaciones para registrar y consultar asistencias")
@RestController
@RequestMapping("/api/assistance/records")
@RequiredArgsConstructor
public class AssistanceRecordController {

    private final AssistanceRecordService recordService;

    @Operation(
            summary = "Registrar entrada por QR",
            description = "Marca la entrada de asistencia para un estudiante. Utilizar el ID leído del QR.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registro actualizado o creado",
                            content = @Content(schema = @Schema(implementation = AssistanceRecordResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validación fallida o plazo expirado", content = @Content)
            }
    )
    @PostMapping("/entry")
    public ResponseEntity<AssistanceRecordResponseDTO> registerEntry(
            @Valid @RequestBody AssistanceRecordCreateDTO dto) {
        AssistanceRecordResponseDTO response = recordService.registerEntry(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registrar salida por QR",
            description = "Marca la salida de asistencia para un estudiante. Utilizar el ID leído del QR.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registro actualizado",
                            content = @Content(schema = @Schema(implementation = AssistanceRecordResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validación fallida o no hay entrada previa", content = @Content)
            }
    )
    @PostMapping("/exit")
    public ResponseEntity<AssistanceRecordResponseDTO> registerExit(
            @Valid @RequestBody AssistanceRecordCreateDTO dto) {
        AssistanceRecordResponseDTO response = recordService.registerExit(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Editar registro de asistencia (opcional, sólo si permitido)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registro editado",
                            content = @Content(schema = @Schema(implementation = AssistanceRecordResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AssistanceRecordResponseDTO> editRecord(
            @PathVariable Long id,
            @Valid @RequestBody AssistanceRecordUpdateDTO updatedData) {
        AssistanceRecordResponseDTO response = recordService.editRecord(id, updatedData);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consultar registros filtrados de asistencia",
            description = "Permite filtrar registros por alumno, fechas, estados, etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de registros",
                            content = @Content(schema = @Schema(implementation = AssistanceRecordResponseDTO.class))),
            }
    )
    @GetMapping
    public Page<AssistanceRecordResponseDTO> getRecordsByFilter(
            @ParameterObject AssistanceReportFilterDTO filter,
            @ParameterObject Pageable pageable
    ) {
        return recordService.getRecordsByFilter(filter, pageable);
    }
}
