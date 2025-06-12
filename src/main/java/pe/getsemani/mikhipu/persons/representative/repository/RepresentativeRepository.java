package pe.getsemani.mikhipu.persons.representative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;

import java.util.Optional;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Long> {
    Optional<Representative> findByPerson_User_Username(String username);
}
