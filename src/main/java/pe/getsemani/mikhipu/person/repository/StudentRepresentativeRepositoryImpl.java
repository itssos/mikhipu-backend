package pe.getsemani.mikhipu.person.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class StudentRepresentativeRepositoryImpl implements StudentRepresentativeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void removeRepresentativesFromStudent(Long studentId, Set<Long> representativeIds) {
        String sql = "DELETE FROM student_representative WHERE student_id = :studentId AND representative_id IN (:repIds)";
        jdbcTemplate.update(sql,
                new MapSqlParameterSource()
                        .addValue("studentId", studentId)
                        .addValue("repIds", representativeIds)
        );
    }
}