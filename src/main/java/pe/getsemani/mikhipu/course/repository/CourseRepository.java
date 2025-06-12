package pe.getsemani.mikhipu.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentCourseViewProjection;

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

    List<Course> findByMainTeacher_Id(Long teacherId);

    List<Course> findByTeachers_Id(Long teacherId);

    boolean existsByIdAndStudents_Id(Long courseId, Long studentId);

    List<Course> findByStudents_Id(Long studentId);


}