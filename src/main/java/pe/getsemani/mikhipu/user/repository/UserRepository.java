package pe.getsemani.mikhipu.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
