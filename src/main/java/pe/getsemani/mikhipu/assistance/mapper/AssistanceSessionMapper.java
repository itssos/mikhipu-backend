package pe.getsemani.mikhipu.assistance.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceSessionResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssistanceSessionMapper {

    public List<AssistanceSession> fromCreateDto(AssistanceSessionCreateDTO dto) {
        return dto.getDates().stream().map(date ->
                AssistanceSession.builder()
                        .date(date)
                        .startEntryTime(dto.getStartEntryTime())
                        .endEntryTime(dto.getEndEntryTime())
                        .startExitTime(dto.getStartExitTime())
                        .endExitTime(dto.getEndExitTime())
                        .attendanceDeadline(dto.getAttendanceDeadline())
                        .build()
        ).collect(Collectors.toList());
    }

    public AssistanceSessionResponseDTO toResponseDto(AssistanceSession entity) {
        AssistanceSessionResponseDTO dto = new AssistanceSessionResponseDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setStartEntryTime(entity.getStartEntryTime());
        dto.setEndEntryTime(entity.getEndEntryTime());
        dto.setStartExitTime(entity.getStartExitTime());
        dto.setEndExitTime(entity.getEndExitTime());
        dto.setAttendanceDeadline(entity.getAttendanceDeadline());
        return dto;
    }
}
