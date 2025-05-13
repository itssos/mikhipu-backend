package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.dto.basic.RepresentativeBasicProjection;
import pe.getsemani.mikhipu.person.entity.Student;

import java.lang.ScopedValue;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    <T> ScopedValue<T> findByPersonDni(String dni);
    @Query(value = """
    SELECT r.id AS id,
           CONCAT(p.first_name, ' ', p.last_name) AS fullName
    FROM student_representative sr
    JOIN representatives r ON r.id = sr.representative_id
    JOIN persons p ON p.id = r.person_id
    WHERE sr.student_id = :studentId
    """, nativeQuery = true)
    List<RepresentativeBasicProjection> findRepresentativesByStudentId(@Param("studentId") Long studentId);

    @Query(value = """
    SELECT s.id AS id,
           CONCAT(p.first_name, ' ', p.last_name) AS fullName,
           p.dni AS dni,
           s.grade AS grade,
           s.section AS section,
           s.school_level AS schoolLevel
    FROM students s
    JOIN persons p ON s.person_id = p.id
    WHERE (:dni IS NULL OR p.dni = :dni)
      AND (:name IS NULL OR CONCAT(p.first_name, ' ', p.last_name) ILIKE %:name%)
    """, nativeQuery = true)
    List<StudentCourseViewProjection> findByOptionalFilters(@Param("dni") String dni, @Param("name") String name);


}
