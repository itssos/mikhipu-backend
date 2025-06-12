package pe.getsemani.mikhipu.persons.teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByCode(String code);

    Optional<Teacher> findByCode(String code);

    List<Teacher> findAllByCodeIn(Set<String> codes);

    Optional<Teacher> findByPerson_User_Username(String username);
}
