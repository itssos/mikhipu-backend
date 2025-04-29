package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.dto.basic.StudentBasicDTO;
import pe.getsemani.mikhipu.person.enums.RelationshipType;

import java.util.Set;

@Data
public class RepresentativeResponseDTO {
    private Long id;
    private PersonResponseDTO person;
    private RelationshipType relationship;
    private Set<StudentBasicDTO> students;
}