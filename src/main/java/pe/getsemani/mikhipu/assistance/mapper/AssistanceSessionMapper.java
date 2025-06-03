package pe.getsemani.mikhipu.assistance.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class AssistanceSessionMapper {

    public AssistanceSession fromCreateDto(AssistanceSessionCreateDTO dto) {
        return AssistanceSession.builder()
                .startEntryTime(dto.getStartEntryTime())
                .endEntryTime(dto.getEndEntryTime())
                .startExitTime(dto.getStartExitTime())
                .endExitTime(dto.getEndExitTime())
                .attendanceDeadline(dto.getAttendanceDeadline())
                .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)
                .build();
    }

    public AssistanceSession fromResponseDto(AssistanceSessionResponseDTO dto) {
        return AssistanceSession.builder()
                .id(dto.getId())
                .startEntryTime(dto.getStartEntryTime())
                .endEntryTime(dto.getEndEntryTime())
                .startExitTime(dto.getStartExitTime())
                .endExitTime(dto.getEndExitTime())
                .attendanceDeadline(dto.getAttendanceDeadline())
                .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)
                .build();
    }

    public AssistanceSessionResponseDTO toResponseDto(AssistanceSession entity) {
        AssistanceSessionResponseDTO dto = new AssistanceSessionResponseDTO();
        dto.setId(entity.getId());
        dto.setStartEntryTime(entity.getStartEntryTime());
        dto.setEndEntryTime(entity.getEndEntryTime());
        dto.setStartExitTime(entity.getStartExitTime());
        dto.setEndExitTime(entity.getEndExitTime());
        dto.setAttendanceDeadline(entity.getAttendanceDeadline());
        dto.setActive(entity.getActive());
        return dto;
    }
}
