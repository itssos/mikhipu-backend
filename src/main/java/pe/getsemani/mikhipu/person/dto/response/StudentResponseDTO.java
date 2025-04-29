package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.dto.basic.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;

import java.util.Set;

@Data
public class StudentResponseDTO {
    private Long id;
    private PersonResponseDTO person;
    private Integer grade;
    private Section section;
    private SchoolLevel schoolLevel;
    private Set<RepresentativeBasicDTO> representatives;
}