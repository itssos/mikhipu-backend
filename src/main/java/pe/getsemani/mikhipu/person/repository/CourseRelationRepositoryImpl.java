package pe.getsemani.mikhipu.person.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CourseRelationRepositoryImpl implements CourseRelationRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void assignAuxiliaryTeachers(Long courseId, Set<Long> teacherIds) {
        String sql = "INSERT INTO course_teacher (course_id, teacher_id) VALUES (:courseId, :teacherId)";
        for (Long teacherId : teacherIds) {
            jdbc.update(sql, new MapSqlParameterSource()
                    .addValue("courseId", courseId)
                    .addValue("teacherId", teacherId));
        }
    }

    @Override
    public void removeTeachersByCode(Long courseId, Set<String> teacherCodes) {
        String sql = """
            DELETE FROM course_teacher
            WHERE course_id = :courseId
              AND teacher_id IN (
                SELECT id FROM teachers WHERE code IN (:codes)
              )
        """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("codes", teacherCodes));
    }

    @Override
    public void assignStudents(Long courseId, Set<Long> studentIds) {
        String existingQuery = """
        SELECT student_id FROM course_student
        WHERE course_id = :courseId AND student_id IN (:studentIds)
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("studentIds", studentIds);

        List<Long> alreadyAssigned = jdbc.queryForList(existingQuery, params, Long.class);

        Set<Long> newIds = new HashSet<>(studentIds);
        newIds.removeAll(alreadyAssigned);

        if (newIds.isEmpty()) return;

        String insertSql = "INSERT INTO course_student (course_id, student_id) VALUES (:courseId, :studentId)";
        for (Long id : newIds) {
            jdbc.update(insertSql, new MapSqlParameterSource()
                    .addValue("courseId", courseId)
                    .addValue("studentId", id));
        }
    }


    @Override
    public void removeStudents(Long courseId, Set<Long> studentIds) {
        String sql = "DELETE FROM course_student WHERE course_id = :courseId AND student_id IN (:ids)";
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("ids", studentIds));
    }
}
