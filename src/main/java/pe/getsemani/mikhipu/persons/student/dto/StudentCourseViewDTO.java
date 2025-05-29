package pe.getsemani.mikhipu.persons.student.dto;

import lombok.Data;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;

@Data
public class StudentCourseViewDTO {
    private Long id;
    private String fullName;
    private String dni;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
}