package pe.getsemani.mikhipu.persons.student.dto;

import lombok.Data;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;

import java.util.Set;

@Data
public class StudentCreateDTO {
    private PersonCreateDTO person;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
    private Set<Long> representativeIds;
}