package pe.getsemani.mikhipu.persons.student.dto;

import lombok.Data;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;

@Data
public class StudentResponseDTO {
    private Long id;
    private PersonResponseDTO person;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
}