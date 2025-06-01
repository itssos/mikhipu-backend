package pe.getsemani.mikhipu.assistance.specification;

import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDate;

public class AssistanceRecordSpecification {

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
                return cb.between(root.get("session").get("date"), startDate, endDate);
            if (startDate != null)
                return cb.greaterThanOrEqualTo(root.get("session").get("date"), startDate);
            return cb.lessThanOrEqualTo(root.get("session").get("date"), endDate);
        };
    }

    public static Specification<AssistanceRecord> hasSessionId(Long sessionId) {
        return (root, query, cb) -> sessionId == null ? null : cb.equal(root.get("session").get("id"), sessionId);
    }

    public static Specification<AssistanceRecord> hasDate(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.equal(root.get("session").get("date"), date);
    }
}
