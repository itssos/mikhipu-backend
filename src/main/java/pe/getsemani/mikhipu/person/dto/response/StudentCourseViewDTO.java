package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;

@Data
public class StudentCourseViewDTO {
    private Long id;
    private String fullName;
    private String dni;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
}