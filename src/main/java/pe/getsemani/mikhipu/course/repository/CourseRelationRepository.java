package pe.getsemani.mikhipu.course.repository;

import java.util.Set;

public interface CourseRelationRepository {
    void assignAuxiliaryTeachers(Long courseId, Set<Long> teacherIds);
    void removeTeachersByCode(Long courseId, Set<String> teacherCodes);
    void assignStudents(Long courseId, Set<Long> studentIds);
    void removeStudents(Long courseId, Set<Long> studentIds);
}