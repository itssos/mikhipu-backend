package pe.getsemani.mikhipu.scores.repository;

import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.scores.entity.Evaluation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long>, JpaSpecificationExecutor<Evaluation> {

    Page<Evaluation> findAllByCourse_Id(Long courseId, Pageable pageable);

    boolean existsByCourse_IdAndNameAndDate(Long courseId, String name, LocalDate date);

}
