package pe.getsemani.mikhipu.assistance.specification;

import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDate;

public class AssistanceRecordSpecification {

    // Entry-point para filtrar usando el DTO
    public static Specification<AssistanceRecord> buildFromFilter(AssistanceReportFilterDTO filter) {
        return Specification
                .where(hasStudentId(filter.getStudentId()))
                .and(hasEntryStatus(filter.getEntryStatus()))
                .and(hasExitStatus(filter.getExitStatus()))
                .and(dateBetween(filter.getStartDate(), filter.getEndDate()));
    }

    public static Specification<AssistanceRecord> hasStudentId(Long studentId) {
        return (root, query, cb) -> studentId == null ? null : cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<AssistanceRecord> hasEntryStatus(AssistanceEntryStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("entryStatus"), status);
    }

    public static Specification<AssistanceRecord> hasExitStatus(AssistanceExitStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("exitStatus"), status);
    }

    public static Specification<AssistanceRecord> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate != null && endDate != null)
                return cb.between(root.get("date"), startDate, endDate);
            if (startDate != null)
                return cb.greaterThanOrEqualTo(root.get("date"), startDate);
            return cb.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }

    public static Specification<AssistanceRecord> hasDate(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.equal(root.get("date"), date);
    }
}
