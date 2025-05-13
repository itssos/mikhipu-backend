package pe.getsemani.mikhipu.person.repository;

import java.util.Set;

public interface CourseStudentRepository {
    void removeStudentsFromCourse(Long courseId, Set<Long> studentIds);
    void removeTeachersFromCourseByCode(Long courseId, Set<String> teacherCodes);
}