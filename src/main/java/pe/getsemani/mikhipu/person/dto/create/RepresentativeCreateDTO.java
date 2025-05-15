package pe.getsemani.mikhipu.person.dto.create;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;

import java.util.Set;

@Data
public class RepresentativeCreateDTO {
    private PersonCreateDTO person;
    private RelationshipType relationship;
    private Set<Long> studentIds;
}