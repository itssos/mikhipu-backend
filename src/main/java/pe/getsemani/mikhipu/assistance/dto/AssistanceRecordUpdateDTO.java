package pe.getsemani.mikhipu.assistance.dto;

import lombok.Data;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDateTime;

@Data
public class AssistanceRecordUpdateDTO {
    private LocalDateTime entryMarkedAt;
    private AssistanceEntryStatus entryStatus;
    private LocalDateTime exitMarkedAt;
    private AssistanceExitStatus exitStatus;
    private Boolean edited;
}
