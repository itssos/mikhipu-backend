package pe.getsemani.mikhipu.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssistanceRecordRepository extends JpaRepository<AssistanceRecord, Long> {

    List<AssistanceRecord> findByStudentId(Long studentId);

    List<AssistanceRecord> findByStudentIdAndSession_DateBetween(Long studentId, LocalDate start, LocalDate end);

    List<AssistanceRecord> findBySession_Id(Long sessionId);

    Optional<AssistanceRecord> findByStudentIdAndSession_Id(Long studentId, Long sessionId);

    List<AssistanceRecord> findByEntryStatus(AssistanceEntryStatus entryStatus);

    List<AssistanceRecord> findByExitStatus(AssistanceExitStatus exitStatus);

    List<AssistanceRecord> findByStudentIdAndSession_Date(Long studentId, LocalDate date);
}
