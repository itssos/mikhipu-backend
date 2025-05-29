package pe.getsemani.mikhipu.course.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CourseStudentRepositoryImpl implements CourseStudentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void removeStudentsFromCourse(Long courseId, Set<Long> studentIds) {
        String sql = "DELETE FROM course_student WHERE course_id = :courseId AND student_id IN (:studentIds)";
        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("studentIds", studentIds));
    }

    @Override
    public void removeTeachersFromCourseByCode(Long courseId, Set<String> teacherCodes) {
        String sql = """
            DELETE FROM course_teacher
            WHERE course_id = :courseId
              AND teacher_id IN (
                SELECT id FROM teachers WHERE code IN (:codes)
              )
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("codes", teacherCodes);

        jdbcTemplate.update(sql, params);
    }
}
