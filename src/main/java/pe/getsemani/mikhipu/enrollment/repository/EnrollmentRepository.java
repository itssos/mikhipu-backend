package pe.getsemani.mikhipu.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.enrollment.entity.Enrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudent_IdAndYear(Long studentId, String year);


    List<Enrollment> findAllByStudentId(Long studentId);

    boolean existsByStudent_IdAndYear(Long studentId, String year);
}
