package pe.getsemani.mikhipu.assistance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;
import pe.getsemani.mikhipu.assistance.mapper.AssistanceSessionMapper;
import pe.getsemani.mikhipu.assistance.repository.AssistanceSessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistanceSessionService {

    private final AssistanceSessionRepository sessionRepository;
    private final AssistanceSessionMapper sessionMapper;

    /**
     * Crea o actualiza la configuración global de asistencia.
     * Solo debe existir una configuración activa.
     */
    @Transactional
    public AssistanceSessionResponseDTO saveSession(AssistanceSessionCreateDTO dto) {
        AssistanceSession session = sessionMapper.fromCreateDto(dto);
        AssistanceSession saved = sessionRepository.save(session);
        return sessionMapper.toResponseDto(saved);
    }

    @Transactional
    public AssistanceSessionResponseDTO updateSession(Long id, AssistanceSessionCreateDTO dto) {
        AssistanceSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la configuración de asistencia con id: " + id));

        session.setStartEntryTime(dto.getStartEntryTime());
        session.setEndEntryTime(dto.getEndEntryTime());
        session.setStartExitTime(dto.getStartExitTime());
        session.setEndExitTime(dto.getEndExitTime());
        session.setAttendanceDeadline(dto.getAttendanceDeadline());
        if (dto.getActive() != null) {
            session.setActive(dto.getActive());
        }

        AssistanceSession updated = sessionRepository.save(session);
        return sessionMapper.toResponseDto(updated);
    }

    /**
     * Obtiene la configuración global activa.
     * Lanza excepción si no existe.
     */
    public AssistanceSessionResponseDTO getActiveSessions() {
        AssistanceSession session = sessionRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("No existe configuración de asistencia activa."));
        return sessionMapper.toResponseDto(session);
    }

}
