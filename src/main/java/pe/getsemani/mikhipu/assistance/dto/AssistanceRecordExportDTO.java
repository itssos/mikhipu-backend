package pe.getsemani.mikhipu.assistance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

public interface AssistanceRecordExportDTO {
    Long getStudentId();
    String getFirstName();
    String getLastName();
    LocalDate getDate();
    LocalDateTime getEntryMarkedAt();
    AssistanceEntryStatus getEntryStatus();
    LocalDateTime getExitMarkedAt();
    AssistanceExitStatus getExitStatus();
    Boolean getEdited();
}
