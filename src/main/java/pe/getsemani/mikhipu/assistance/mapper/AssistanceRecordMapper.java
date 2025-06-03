package pe.getsemani.mikhipu.assistance.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordCreateDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordResponseDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.persons.student.entity.Student;

import java.time.LocalDate;

@Component
public class AssistanceRecordMapper {

    public AssistanceRecord fromCreateDto(AssistanceRecordCreateDTO dto, Student student, LocalDate date) {
        return AssistanceRecord.builder()
                .student(student)
                .date(date)
                .edited(false)
                .build();
    }

    public AssistanceRecordResponseDTO toResponseDto(AssistanceRecord entity) {
        AssistanceRecordResponseDTO dto = new AssistanceRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudent().getId());
        dto.setFirstName(entity.getStudent().getPerson().getFirstName());
        dto.setLastName(entity.getStudent().getPerson().getLastName());
        dto.setDate(entity.getDate());
        dto.setEntryMarkedAt(entity.getEntryMarkedAt());
        dto.setEntryStatus(entity.getEntryStatus());
        dto.setExitMarkedAt(entity.getExitMarkedAt());
        dto.setExitStatus(entity.getExitStatus());
        dto.setEdited(entity.getEdited());
        dto.setGrade(entity.getStudent().getGrade());
        dto.setSection(entity.getStudent().getSection());
        dto.setSchoolLevel(entity.getStudent().getSchoolLevel());
        return dto;
    }
}
