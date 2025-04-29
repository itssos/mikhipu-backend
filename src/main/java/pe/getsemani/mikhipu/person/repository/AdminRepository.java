package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
