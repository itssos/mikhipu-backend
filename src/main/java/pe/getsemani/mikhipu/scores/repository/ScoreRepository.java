package pe.getsemani.mikhipu.scores.repository;

import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.scores.entity.Evaluation;
import pe.getsemani.mikhipu.persons.student.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pe.getsemani.mikhipu.scores.entity.Score;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

    List<Score> findByEvaluation_Id(Long evaluationId);

    List<Score> findByEvaluation_Course_Id(Long courseId);

    Page<Score> findAllByStudent_Id(Long studentId, Pageable pageable);

    // Para evitar duplicados al registrar nota
    boolean existsByStudentAndEvaluation(Student student, Evaluation evaluation);

    // Otros métodos útiles
    void deleteByEvaluation(Evaluation evaluation);
}
