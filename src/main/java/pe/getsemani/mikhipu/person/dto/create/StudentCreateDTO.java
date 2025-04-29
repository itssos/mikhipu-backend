package pe.getsemani.mikhipu.person.dto.create;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;

import java.util.Set;

@Data
public class StudentCreateDTO {
    private PersonCreateDTO person;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
    private Set<Long> representativeIds;
}