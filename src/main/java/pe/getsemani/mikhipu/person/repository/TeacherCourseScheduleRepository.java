package pe.getsemani.mikhipu.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.person.entity.TeacherCourseSchedule;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherCourseScheduleRepository extends JpaRepository<TeacherCourseSchedule, Long> {
    List<TeacherCourseSchedule> findByTeacherId(Long teacherId);

    List<TeacherCourseSchedule> findByCourseId(Long courseId);

    List<TeacherCourseSchedule> findByTeacherIdAndCourseId(Long teacherId, Long courseId);

    Optional<TeacherCourseSchedule> findByTeacherIdAndCourseIdAndDayOfWeek(Long teacherId, Long courseId, DayOfWeek dayOfWeek);
}