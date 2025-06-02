package pe.getsemani.mikhipu.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.assistance.dto.AssistanceRecordExportDTO;
import pe.getsemani.mikhipu.assistance.dto.AssistanceStatisticsDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssistanceRecordRepository extends JpaRepository<AssistanceRecord, Long>, JpaSpecificationExecutor<AssistanceRecord> {

    List<AssistanceRecord> findByStudentId(Long studentId);

    List<AssistanceRecord> findByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

    Optional<AssistanceRecord> findByStudentIdAndDate(Long studentId, LocalDate date);

    List<AssistanceRecord> findByEntryStatus(AssistanceEntryStatus entryStatus);

    List<AssistanceRecord> findByExitStatus(AssistanceExitStatus exitStatus);

    List<AssistanceRecord> findByDate(LocalDate date);

    @Query("""
        SELECT 
          s.id as studentId,
          p.firstName as firstName,
          p.lastName as lastName,
          r.date as date,
          r.entryMarkedAt as entryMarkedAt,
          r.entryStatus as entryStatus,
          r.exitMarkedAt as exitMarkedAt,
          r.exitStatus as exitStatus,
          r.edited as edited
        FROM AssistanceRecord r
        JOIN r.student s
        JOIN s.person p
        WHERE (:studentId IS NULL OR s.id = :studentId)
          AND (:startDate IS NULL OR r.date >= :startDate)
          AND (:endDate IS NULL OR r.date <= :endDate)
          AND (:entryStatus IS NULL OR r.entryStatus = :entryStatus)
          AND (:exitStatus IS NULL OR r.exitStatus = :exitStatus)
    """)
    List<AssistanceRecordExportDTO> findForExport(
            Long studentId,
            LocalDate startDate,
            LocalDate endDate,
            AssistanceEntryStatus entryStatus,
            AssistanceExitStatus exitStatus
    );

    /**
     * Estadísticas agregadas por estados para un estudiante dentro de un rango de fechas.
     */
    @Query("""
    SELECT new pe.getsemani.mikhipu.assistance.dto.AssistanceStatisticsDTO(
            s.id,
            CONCAT(p.firstName, ' ', p.lastName),
            COUNT(r),
            SUM(CASE WHEN r.entryStatus = 'PRESENTE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.entryStatus = 'TARDANZA' THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.entryStatus = 'AUSENTE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.exitStatus = 'SALIDA_REGULAR' THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.exitStatus = 'SALIDA_ANTICIPADA' THEN 1 ELSE 0 END),
            ROUND(
                (SUM(CASE WHEN r.entryStatus = 'PRESENTE' OR r.entryStatus = 'TARDANZA' THEN 1.0 ELSE 0 END) * 100.0) /
                COUNT(r),
                2
            )
        )
        FROM AssistanceRecord r
        JOIN r.student s
        JOIN s.person p
        WHERE (:studentId IS NULL OR s.id = :studentId)
          AND (:startDate IS NULL OR r.date >= :startDate)
          AND (:endDate IS NULL OR r.date <= :endDate)
        GROUP BY s.id, p.firstName, p.lastName
    """)
    List<AssistanceStatisticsDTO> getStatistics(Long studentId, LocalDate startDate, LocalDate endDate);

}
