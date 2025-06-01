package pe.getsemani.mikhipu.assistance.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;

@Component
public class AssistanceRecordMapper {

    public AssistanceRecordResponseDTO toResponseDto(AssistanceRecord entity) {
        AssistanceRecordResponseDTO dto = new AssistanceRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudent().getId());
        dto.setSessionId(entity.getSession().getId());
        dto.setEntryMarkedAt(entity.getEntryMarkedAt());
        dto.setEntryStatus(entity.getEntryStatus());
        dto.setExitMarkedAt(entity.getExitMarkedAt());
        dto.setExitStatus(entity.getExitStatus());
        return dto;
    }
}
