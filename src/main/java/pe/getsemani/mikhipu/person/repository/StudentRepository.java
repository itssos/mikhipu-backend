package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.Student;

import java.lang.ScopedValue;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    <T> ScopedValue<T> findByPersonDni(String dni);
}
