package pe.getsemani.mikhipu.assistance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordUpdateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.assistance.mapper.AssistanceRecordMapper;
import pe.getsemani.mikhipu.assistance.mapper.AssistanceSessionMapper;
import pe.getsemani.mikhipu.assistance.repository.AssistanceRecordRepository;
import pe.getsemani.mikhipu.assistance.repository.AssistanceSessionRepository;
import pe.getsemani.mikhipu.assistance.specification.AssistanceRecordSpecification;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistanceRecordService {

    private final AssistanceRecordRepository recordRepository;
    private final AssistanceSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final AssistanceRecordMapper recordMapper;
    private final AssistanceSessionService assistanceSessionService;
    private final AssistanceSessionMapper assistanceSessionMapper;


    // Registra la entrada para un estudiante en la fecha actual
    @Transactional
    public AssistanceRecordResponseDTO registerEntry(AssistanceRecordCreateDTO dto) {
        AssistanceSessionResponseDTO config = assistanceSessionService.getActiveSessions();

        LocalTime nowTime = LocalTime.now();
        LocalDateTime nowDateTime = LocalDateTime.now();

        // Validación DEADLINE
        if (config.getAttendanceDeadline() != null && nowDateTime.isAfter(config.getAttendanceDeadline())) {
            throw new IllegalStateException("El plazo para registrar asistencia ha expirado.");
        }

        // Antes de startEntryTime: no permitido
        if (nowTime.isBefore(config.getStartEntryTime())) {
            throw new IllegalArgumentException("No puedes marcar entrada antes del horario permitido.");
        }

        // Después de endEntryTime pero antes de startExitTime: TARDANZA permitida
        boolean isTardanza = nowTime.isAfter(config.getEndEntryTime()) && nowTime.isBefore(config.getStartExitTime());

        // Después del startExitTime: no permitido
        if (nowTime.isAfter(config.getStartExitTime()) || nowTime.equals(config.getStartExitTime())) {
            throw new IllegalArgumentException("Ya no puedes marcar entrada, el horario ha finalizado.");
        }

        LocalDate today = LocalDate.now();
        AssistanceRecord record = recordRepository.findByStudentIdAndDate(dto.getStudentId(), today)
                .orElseGet(() -> AssistanceRecord.builder()
                        .student(studentRepository.getReferenceById(dto.getStudentId()))
                        .date(today)
                        .build());

        record.setEntryMarkedAt(nowDateTime);

        // Determina el estado según la hora
        if (nowTime.isAfter(config.getEndEntryTime()) && nowTime.isBefore(config.getStartExitTime())) {
            record.setEntryStatus(AssistanceEntryStatus.TARDANZA);
        } else {
            record.setEntryStatus(AssistanceEntryStatus.PRESENTE);
        }

        recordRepository.save(record);

        return recordMapper.toResponseDto(record);
    }

    // Registra la salida para un estudiante en la fecha actual
    @Transactional
    public AssistanceRecordResponseDTO registerExit(AssistanceRecordCreateDTO dto) {
        AssistanceSessionResponseDTO config = assistanceSessionService.getActiveSessions();

        LocalTime nowTime = LocalTime.now();
        LocalDateTime nowDateTime = LocalDateTime.now();

        // Validación de horario de salida
        if (nowTime.isBefore(config.getStartExitTime())) {
            throw new IllegalArgumentException("No puedes marcar salida antes del horario permitido.");
        }
        if (nowTime.isAfter(config.getEndExitTime())) {
            throw new IllegalArgumentException("No puedes marcar salida, ya pasó el horario permitido.");
        }

        LocalDate today = LocalDate.now();
        AssistanceRecord record = recordRepository.findByStudentIdAndDate(dto.getStudentId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un registro de entrada para este alumno."));

        record.setExitMarkedAt(nowDateTime);
        record.setExitStatus(getExitStatus(nowTime, assistanceSessionMapper.fromResponseDto(config)));
        recordRepository.save(record);

        return recordMapper.toResponseDto(record);
    }

    @Transactional
    public AssistanceRecordResponseDTO editRecord(Long recordId, AssistanceRecordUpdateDTO dto) {
        AssistanceRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado."));

        AssistanceSession config = sessionRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("No existe configuración de asistencia activa."));

        if (config.getAttendanceDeadline() != null && LocalDateTime.now().isAfter(config.getAttendanceDeadline())) {
            throw new IllegalStateException("Ya no se puede editar este registro.");
        }

        // Puedes usar el mapper o settear manualmente si son pocos campos
        record.setEntryMarkedAt(dto.getEntryMarkedAt());
        record.setEntryStatus(dto.getEntryStatus());
        record.setExitMarkedAt(dto.getExitMarkedAt());
        record.setExitStatus(dto.getExitStatus());
        record.setEdited(dto.getEdited());

        recordRepository.save(record);

        return recordMapper.toResponseDto(record);
    }


    public List<AssistanceRecordResponseDTO> getRecordsByFilter(AssistanceReportFilterDTO filter) {
        return recordRepository.findAll(AssistanceRecordSpecification.buildFromFilter(filter)).stream()
                .map(recordMapper::toResponseDto)
                .toList();
    }

    // Marca ausentes a todos los alumnos que no marcaron entrada en la fecha
    @Transactional
    public void closeDayAndMarkAbsences(LocalDate date) {
        List<AssistanceRecord> records = recordRepository.findByDate(date);
        for (AssistanceRecord record : records) {
            if (record.getEntryStatus() == AssistanceEntryStatus.NO_MARCADA) {
                record.setEntryStatus(AssistanceEntryStatus.AUSENTE);
            }
            // Opcional: lógica para salidas no marcadas.
        }
        recordRepository.saveAll(records);
    }

    // PRIVATE (No endpoint)
    private AssistanceEntryStatus getEntryStatus(LocalTime time, AssistanceSession config) {
        if (time.isBefore(config.getStartEntryTime())) return AssistanceEntryStatus.NO_MARCADA;
        if (!time.isAfter(config.getEndEntryTime())) return AssistanceEntryStatus.PRESENTE;
        return AssistanceEntryStatus.TARDANZA;
    }

    private AssistanceExitStatus getExitStatus(LocalTime time, AssistanceSession config) {
        if (time.isBefore(config.getStartExitTime())) return AssistanceExitStatus.SALIDA_ANTICIPADA;
        if (time.isAfter(config.getEndExitTime())) return AssistanceExitStatus.NO_MARCADA;
        return AssistanceExitStatus.SALIDA_REGULAR;
    }
}
