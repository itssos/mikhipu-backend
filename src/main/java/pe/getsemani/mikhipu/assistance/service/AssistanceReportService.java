package pe.getsemani.mikhipu.assistance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordExportDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceStatisticsDTO;
import pe.getsemani.mikhipu.assistance.mapper.AssistanceRecordMapper;
import pe.getsemani.mikhipu.assistance.repository.AssistanceRecordRepository;
import pe.getsemani.mikhipu.assistance.specification.AssistanceRecordSpecification;
import pe.getsemani.mikhipu.persons.student.service.StudentService;
import pe.getsemani.mikhipu.util.ExportUtil; // <-- tu nuevo util general

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistanceReportService {

    private final AssistanceRecordRepository recordRepository;
    private final AssistanceRecordMapper recordMapper;
    private final StudentService studentService;

    /**
     * Consulta de registros filtrados para reportes.
     */
    public List<AssistanceRecordResponseDTO> getRecordsByFilter(AssistanceReportFilterDTO filter) {
        return recordRepository.findAll(AssistanceRecordSpecification.buildFromFilter(filter)).stream()
                .map(recordMapper::toResponseDto)
                .toList();
    }

    /**
     * Estadísticas por alumno o rango de alumnos.
     */
    public List<AssistanceStatisticsDTO> getStatistics(AssistanceReportFilterDTO filter) {
        return recordRepository.getStatistics(
                filter.getStudentId(),
                filter.getStartDate(),
                filter.getEndDate()
        );
    }

    /**
     * Exportar registros filtrados a Excel.
     */
    public byte[] exportToExcel(AssistanceReportFilterDTO filter) {
        List<AssistanceRecordExportDTO> records = recordRepository.findForExport(
                filter.getStudentId(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getEntryStatus(),
                filter.getExitStatus()
        );

        List<String> headers = List.of(
                "Nombre", "Apellido", "Fecha",
                "Entrada", "Estado Entrada", "Salida", "Estado Salida"
        );

        List<List<Object>> rows = records.stream()
                .map(r -> Arrays.asList(
                        (Object) (r.getFirstName() != null ? r.getFirstName() : ""),
                        (Object) (r.getLastName() != null ? r.getLastName() : ""),
                        (Object) (r.getDate() != null ? r.getDate().toString() : ""),
                        (Object) (r.getEntryMarkedAt() != null ? r.getEntryMarkedAt().toString() : ""),
                        (Object) (r.getEntryStatus() != null ? r.getEntryStatus().toString() : ""),
                        (Object) (r.getExitMarkedAt() != null ? r.getExitMarkedAt().toString() : ""),
                        (Object) (r.getExitStatus() != null ? r.getExitStatus().toString() : "")
                ))
                .toList();

        String title = (filter.getStudentId() != null && !records.isEmpty())
                ? "Asistencias de " + records.get(0).getFirstName() + " " + records.get(0).getLastName()
                : "Asistencias";

        return ExportUtil.exportToExcel(headers, rows, title);
    }


    /**
     * Exportar registros filtrados a PDF.
     */
    public byte[] exportToPdf(AssistanceReportFilterDTO filter) {
        List<AssistanceRecordExportDTO> records = recordRepository.findForExport(
                filter.getStudentId(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getEntryStatus(),
                filter.getExitStatus()
        );

        List<String> headers = List.of(
                "Nombre", "Apellido", "Fecha",
                "Entrada", "Estado Entrada", "Salida", "Estado Salida"
        );

        List<List<Object>> rows = records.stream()
                .map(r -> Arrays.asList(
                        (Object) (r.getFirstName() != null ? r.getFirstName() : ""),
                        (Object) (r.getLastName() != null ? r.getLastName() : ""),
                        (Object) (r.getDate() != null ? r.getDate().toString() : ""),
                        (Object) (r.getEntryMarkedAt() != null ? r.getEntryMarkedAt().toString() : ""),
                        (Object) (r.getEntryStatus() != null ? r.getEntryStatus().toString() : ""),
                        (Object) (r.getExitMarkedAt() != null ? r.getExitMarkedAt().toString() : ""),
                        (Object) (r.getExitStatus() != null ? r.getExitStatus().toString() : "")
                ))
                .toList();



        String title = (filter.getStudentId() != null && !records.isEmpty())
                ? "Reporte de Asistencias de " + records.get(0).getFirstName() + " " + records.get(0).getLastName()
                : "Reporte de Asistencias";

        return ExportUtil.exportToPdf(headers, rows, title);
    }

}
