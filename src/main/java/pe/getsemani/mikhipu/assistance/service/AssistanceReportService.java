package pe.getsemani.mikhipu.assistance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordExportDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceStatisticsDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.assistance.mapper.AssistanceRecordMapper;
import pe.getsemani.mikhipu.assistance.repository.AssistanceRecordRepository;
import pe.getsemani.mikhipu.assistance.specification.AssistanceRecordSpecification;
import pe.getsemani.mikhipu.assistance.specification.AssistanceRecordStatisticsSpecification;
import pe.getsemani.mikhipu.persons.student.service.StudentService;
import pe.getsemani.mikhipu.util.ExportUtil; // <-- tu nuevo util general

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public Page<AssistanceStatisticsDTO> getStatistics(AssistanceReportFilterDTO filter, Pageable pageable) {
        // 1. Sin paginación aquí
        List<AssistanceRecord> records = recordRepository.findAll(
                AssistanceRecordStatisticsSpecification.buildFromFilter(filter)
        );

        // 2. Agrupa por estudiante
        Map<Long, AssistanceStatisticsDTO> statsMap = new LinkedHashMap<>();
        for (AssistanceRecord record : records) {
            Long studentId = record.getStudent().getId();
            String fullName = record.getStudent().getPerson().getFirstName() + " " +
                    record.getStudent().getPerson().getLastName();

            AssistanceStatisticsDTO dto = statsMap.getOrDefault(studentId,
                    new AssistanceStatisticsDTO(studentId, fullName, 0L, 0L, 0L, 0L, 0L, 0L, 0.0)
            );

            dto.setTotalSessions(dto.getTotalSessions() + 1);
            if (record.getEntryStatus() == AssistanceEntryStatus.PRESENTE)
                dto.setPresentes(dto.getPresentes() + 1);
            if (record.getEntryStatus() == AssistanceEntryStatus.TARDANZA)
                dto.setTardanzas(dto.getTardanzas() + 1);
            if (record.getEntryStatus() == AssistanceEntryStatus.AUSENTE)
                dto.setAusencias(dto.getAusencias() + 1);
            if (record.getExitStatus() == AssistanceExitStatus.SALIDA_REGULAR)
                dto.setSalidasRegulares(dto.getSalidasRegulares() + 1);
            if (record.getExitStatus() == AssistanceExitStatus.SALIDA_ANTICIPADA)
                dto.setSalidasAnticipadas(dto.getSalidasAnticipadas() + 1);

            statsMap.put(studentId, dto);
        }

        // 3. Calcula el porcentaje de asistencia
        statsMap.values().forEach(dto -> {
            Long total = dto.getTotalSessions();
            Long presentes = dto.getPresentes();
            Long tardanzas = dto.getTardanzas();
            dto.setPorcentajeAsistencia(total == 0 ? 0.0 :
                    Math.round(((presentes + tardanzas) * 100.0 / total) * 100.0) / 100.0);
        });

        // 4. Paginación sobre los estudiantes agrupados
        List<AssistanceStatisticsDTO> dtoList = new ArrayList<>(statsMap.values());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        List<AssistanceStatisticsDTO> pageContent = dtoList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dtoList.size());
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
