package pe.getsemani.mikhipu.persons.student.repository;

public interface StudentCourseViewProjection {
    Long getId();
    String getFullName();
    String getDni();
    Integer getGrade();
    String getSection();
    String getSchoolLevel();
}
