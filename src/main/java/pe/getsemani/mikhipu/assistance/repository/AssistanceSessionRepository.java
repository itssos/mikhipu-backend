package pe.getsemani.mikhipu.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.assistance.entity.AssistanceSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssistanceSessionRepository extends JpaRepository<AssistanceSession, Long> {

    Optional<AssistanceSession> findByDate(LocalDate date);

    List<AssistanceSession> findByDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByDate(LocalDate date);
}
