package pe.getsemani.mikhipu.scores.specification;

import pe.getsemani.mikhipu.scores.entity.Evaluation;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EvaluationSpecification {

    public static Specification<Evaluation> hasCourseId(Long courseId) {
        return (root, query, cb) -> courseId == null ? null :
                cb.equal(root.get("course").get("id"), courseId);
    }

    public static Specification<Evaluation> hasYear(String year) {
        return (root, query, cb) -> year == null ? null :
                cb.equal(root.get("course").get("year"), year);
    }

    public static Specification<Evaluation> hasQuarter(String quarter) {
        return (root, query, cb) -> quarter == null ? null :
                cb.equal(root.get("course").get("quarter"), quarter);
    }

    public static Specification<Evaluation> hasType(String type) {
        return (root, query, cb) -> type == null ? null :
                cb.equal(root.get("type"), type);
    }

    public static Specification<Evaluation> hasDateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> (start == null || end == null) ? null :
                cb.between(root.get("date"), start, end);
    }

    public static Specification<Evaluation> hasTeacherId(Long teacherId) {
        return (root, query, cb) -> teacherId == null ? null :
                cb.equal(root.get("course").get("mainTeacher").get("id"), teacherId);
    }

    public static Specification<Evaluation> build(Long courseId, String year, String quarter, String type, LocalDate start, LocalDate end, Long teacherid) {
        return Specification
                .where(hasCourseId(courseId))
                .and(hasYear(year))
                .and(hasQuarter(quarter))
                .and(hasType(type))
                .and(hasDateBetween(start, end))
                .and(hasTeacherId(teacherid));
    }
}
