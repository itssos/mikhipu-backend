package pe.getsemani.mikhipu.assistance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceStatisticsDTO;
import pe.getsemani.mikhipu.assistance.service.AssistanceReportService;

import java.util.List;

@Tag(name = "Reportes y Estadísticas de Asistencia", description = "Reportes, exportaciones y estadísticas")
@RestController
@RequestMapping("/api/assistance/report")
@RequiredArgsConstructor
public class AssistanceReportController {

    private final AssistanceReportService reportService;

    @Operation(
            summary = "Obtener estadísticas de asistencia",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estadísticas generadas",
                            content = @Content(schema = @Schema(implementation = AssistanceStatisticsDTO.class)))
            }
    )
    @GetMapping("/statistics")
    public ResponseEntity<List<AssistanceStatisticsDTO>> getStatistics(@Valid AssistanceReportFilterDTO filter) {
        List<AssistanceStatisticsDTO> result = reportService.getStatistics(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Exportar registros de asistencia a Excel",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo Excel generado", content = @Content),
            }
    )
    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportExcel(@Valid AssistanceReportFilterDTO filter) {
        byte[] excel = reportService.exportToExcel(filter);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=asistencias.xlsx")
                .body(excel);
    }

    @Operation(
            summary = "Exportar registros de asistencia a PDF",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF generado", content = @Content),
            }
    )
    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(@Valid AssistanceReportFilterDTO filter) {
        byte[] pdf = reportService.exportToPdf(filter);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=asistencias.pdf")
                .body(pdf);
    }
}
