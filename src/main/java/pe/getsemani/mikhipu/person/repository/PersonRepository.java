package pe.getsemani.mikhipu.person.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.person.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUserUsername(String username);
}
