package pe.getsemani.mikhipu.persons.person.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.persons.person.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUserUsername(String username);

    Optional<Person> findByDni(String documentNumber);
}
