package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

}
