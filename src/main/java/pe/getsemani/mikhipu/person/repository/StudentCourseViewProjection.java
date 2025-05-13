package pe.getsemani.mikhipu.person.repository;

public interface StudentCourseViewProjection {
    Long getId();
    String getFullName();
    String getDni();
    Integer getGrade();
    String getSection();
    String getSchoolLevel();
}
