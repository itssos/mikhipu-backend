package pe.getsemani.mikhipu.persons.student.specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.dto.StudentFilterDTO;


public class StudentSpecification {

    public static Specification<Student> build(StudentFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // --- Filtro DNI (por la entidad Person) ---
            if (filter.getDni() != null && !filter.getDni().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(root.get("person").get("dni"), "%" + filter.getDni().trim() + "%")
                );
            }

            // --- Filtro nombre ---
            if (filter.getName() != null && !filter.getName().isBlank()) {
                Expression<String> fullNameExpr = cb.concat(
                        cb.lower(root.get("person").get("firstName")),
                        cb.concat(" ", cb.lower(root.get("person").get("lastName")))
                );
                predicate = cb.and(predicate,
                        cb.like(fullNameExpr, "%" + filter.getName().toLowerCase() + "%")
                );
            }

            // --- Filtro grado ---
            if (filter.getGrade() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("grade"), filter.getGrade())
                );
            }

            // --- Filtro sección ---
            if (filter.getSection() != null && !filter.getSection().isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("section"), filter.getSection())
                );
            }

            // --- Filtro nivel escolar ---
            if (filter.getSchoolLevel() != null && !filter.getSchoolLevel().isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("schoolLevel"), filter.getSchoolLevel())
                );
            }

            // --- Filtro por curso (usando subquery) ---
            if (filter.getCourseId() != null) {
                // Subquery: Select s.id from Course c join c.students s where c.id = :courseId
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<?> courseRoot = subquery.from(pe.getsemani.mikhipu.course.entity.Course.class);
                Join<?, Student> joinStudents = courseRoot.join("students", JoinType.INNER);
                subquery.select(joinStudents.get("id"))
                        .where(cb.equal(courseRoot.get("id"), filter.getCourseId()));

                predicate = cb.and(predicate, root.get("id").in(subquery));
            }

            return predicate;
        };
    }
}
