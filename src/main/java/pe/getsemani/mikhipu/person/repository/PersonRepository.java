package pe.getsemani.mikhipu.person.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUserUsername(String username);
}
