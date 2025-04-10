package pe.getsemani.mikhipu.password.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.password.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);
}
