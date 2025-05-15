package pe.getsemani.mikhipu.person.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;

@Component
public class RepresentativeMapper {

    private final PersonMapper personMapper;

    @Autowired
    public RepresentativeMapper(PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    public RepresentativeResponseDTO toDto(Representative representative) {
        if (representative == null) return null;

        RepresentativeResponseDTO dto = new RepresentativeResponseDTO();
        dto.setId(representative.getId());
        dto.setPerson(personMapper.toDto(representative.getPerson()));
        dto.setRelationship(representative.getRelationship());

        return dto;
    }



    public Representative fromCreateDto(RepresentativeCreateDTO dto) {
        if (dto == null) return null;
        Representative representative = new Representative();
        representative.setPerson(personMapper.fromCreateDto(dto.getPerson()));
        representative.setRelationship(dto.getRelationship());
        return representative;
    }
}
