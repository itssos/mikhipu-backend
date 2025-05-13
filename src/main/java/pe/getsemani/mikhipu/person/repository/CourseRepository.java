package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.dto.StudentCourseViewProjection;
import pe.getsemani.mikhipu.person.entity.Course;
import pe.getsemani.mikhipu.person.entity.Student;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("""
        SELECT DISTINCT s FROM Course c
        JOIN c.students s
        WHERE c.mainTeacher.id = :teacherId
           OR :teacherId IN (SELECT t.id FROM c.teachers t)
    """)
    List<Student> findStudentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query(value = """
    SELECT s.id AS id,
           CONCAT(p.first_name, ' ', p.last_name) AS fullName,
           p.dni AS dni,
           s.grade AS grade,
           s.section AS section,
           s.school_level AS schoolLevel
    FROM course_student cs
    JOIN students s ON s.id = cs.student_id
    JOIN persons p ON p.id = s.person_id
    WHERE cs.course_id = :courseId
    """, nativeQuery = true)
    List<StudentCourseViewProjection> findStudentCourseViewByCourseId(@Param("courseId") Long courseId);

    @Query(value = """
    SELECT t.id AS id,
           CONCAT(p.first_name, ' ', p.last_name) AS fullName,
           t.code AS code,
           'PRINCIPAL' AS role
    FROM courses c
    JOIN teachers t ON t.id = c.main_teacher_id
    JOIN persons p ON p.id = t.person_id
    WHERE c.id = :courseId

    UNION ALL

    SELECT t.id AS id,
           CONCAT(p.first_name, ' ', p.last_name) AS fullName,
           t.code AS code,
           'AUXILIAR' AS role
    FROM course_teacher ct
    JOIN teachers t ON t.id = ct.teacher_id
    JOIN persons p ON p.id = t.person_id
    WHERE ct.course_id = :courseId
""", nativeQuery = true)
    List<CourseTeacherViewProjection> findTeachersByCourseId(@Param("courseId") Long courseId);

}