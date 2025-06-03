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

        // === Validaciones de consistencia horaria ===

        // Entrada: inicio < fin
        if (dto.getStartEntryTime().compareTo(dto.getEndEntryTime()) >= 0) {
            throw new IllegalArgumentException("La hora de inicio de entrada debe ser menor que la hora de fin de entrada.");
        }
        // Salida: inicio < fin
        if (dto.getStartExitTime().compareTo(dto.getEndExitTime()) >= 0) {
            throw new IllegalArgumentException("La hora de inicio de salida debe ser menor que la hora de fin de salida.");
        }
        // La entrada debe terminar antes de que inicie la salida
        if (dto.getEndEntryTime().compareTo(dto.getStartExitTime()) > 0) {
            throw new IllegalArgumentException("La hora de fin de entrada debe ser menor o igual que la hora de inicio de salida.");
        }
        // El rango de entrada no debe traslapar el rango de salida
        if (dto.getEndEntryTime().compareTo(dto.getEndExitTime()) > 0) {
            throw new IllegalArgumentException("La hora de fin de entrada no puede ser mayor que la hora de fin de salida.");
        }
        if (dto.getStartEntryTime().compareTo(dto.getEndExitTime()) > 0) {
            throw new IllegalArgumentException("La hora de inicio de entrada no puede ser mayor que la hora de fin de salida.");
        }
        // No permitir que algún rango esté completamente fuera de los demás (opcional)
        if (dto.getStartExitTime().compareTo(dto.getEndEntryTime()) < 0) {
            throw new IllegalArgumentException("La hora de inicio de salida no puede ser menor que la hora de fin de entrada.");
        }

        // ==== Si esta correcto, aplicar ====
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
