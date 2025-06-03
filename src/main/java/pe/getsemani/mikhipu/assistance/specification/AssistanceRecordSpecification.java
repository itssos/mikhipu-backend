package pe.getsemani.mikhipu.assistance.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;

import java.time.LocalDate;

public class AssistanceRecordSpecification {

    // Entry-point para filtrar usando el DTO
    public static Specification<AssistanceRecord> buildFromFilter(AssistanceReportFilterDTO filter) {
        return Specification
                .where(hasStudentId(filter.getStudentId()))
                .and(hasEntryStatus(filter.getEntryStatus()))
                .and(hasExitStatus(filter.getExitStatus()))
                .and(dateBetween(filter.getStartDate(), filter.getEndDate()))
                .and(hasGrade(filter.getGrade()))
                .and(hasSection(filter.getSection()))
                .and(hasSchoolLevel(filter.getSchoolLevel()))
                .and(hasCourseId(filter.getCourseId()));
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

    public static Specification<AssistanceRecord> hasGrade(Integer grade) {
        return (root, query, cb) -> grade == null ? null : cb.equal(root.get("student").get("grade"), grade);
    }

    public static Specification<AssistanceRecord> hasSection(Section section) {
        return (root, query, cb) -> section == null ? null : cb.equal(root.get("student").get("section"), section);
    }

    public static Specification<AssistanceRecord> hasSchoolLevel(SchoolLevel schoolLevel) {
        return (root, query, cb) -> schoolLevel == null ? null : cb.equal(root.get("student").get("schoolLevel"), schoolLevel);
    }

    public static Specification<AssistanceRecord> hasCourseId(Long courseId) {
        return (root, query, cb) -> {
            if (courseId == null) return null;

            // Subquery: select s.id from Course c join c.students s where c.id = :courseId
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Course> courseRoot = subquery.from(pe.getsemani.mikhipu.course.entity.Course.class);
            Join<?, Student> studentsJoin = courseRoot.join("students");
            subquery.select(studentsJoin.get("id"))
                    .where(cb.equal(courseRoot.get("id"), courseId));

            return root.get("student").get("id").in(subquery);
        };
    }
}
