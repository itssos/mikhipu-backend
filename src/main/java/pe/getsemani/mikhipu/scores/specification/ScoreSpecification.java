package pe.getsemani.mikhipu.scores.specification;

import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.scores.entity.Score;

import java.time.LocalDate;

public class ScoreSpecification {

    public static Specification<Score> hasStudentId(Long studentId) {
        return (root, query, cb) -> studentId == null ? null :
                cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<Score> hasEvaluationId(Long evaluationId) {
        return (root, query, cb) -> evaluationId == null ? null :
                cb.equal(root.get("evaluation").get("id"), evaluationId);
    }

    public static Specification<Score> hasCourseId(Long courseId) {
        return (root, query, cb) -> courseId == null ? null :
                cb.equal(root.get("evaluation").get("course").get("id"), courseId);
    }

    public static Specification<Score> hasYear(String year) {
        return (root, query, cb) -> year == null ? null :
                cb.equal(root.get("evaluation").get("course").get("year"), year);
    }

    public static Specification<Score> hasQuarter(String quarter) {
        return (root, query, cb) -> quarter == null ? null :
                cb.equal(root.get("evaluation").get("course").get("quarter"), quarter);
    }

    public static Specification<Score> hasDateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> (start == null || end == null) ? null :
                cb.between(root.get("evaluation").get("date"), start, end);
    }

    public static Specification<Score> hasEvaluationType(String type) {
        return (root, query, cb) -> type == null ? null :
                cb.equal(root.get("evaluation").get("type"), type);
    }
}
