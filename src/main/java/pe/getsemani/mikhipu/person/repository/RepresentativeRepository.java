package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.person.entity.Representative;

public interface RepresentativeRepository extends JpaRepository<Representative, Long> {
}
