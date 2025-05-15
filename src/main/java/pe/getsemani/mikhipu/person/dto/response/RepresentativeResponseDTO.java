package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;

@Data
public class RepresentativeResponseDTO {
    private Long id;
    private PersonResponseDTO person;
    private RelationshipType relationship;
}