package pe.getsemani.mikhipu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.model.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
