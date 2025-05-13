package pe.getsemani.mikhipu.person.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.Teacher;

import java.lang.ScopedValue;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByCode(@NotBlank String code);

    Optional<Teacher> findByCode(String code);

    List<Teacher> findAllByCodeIn(Set<String> codes);
}
