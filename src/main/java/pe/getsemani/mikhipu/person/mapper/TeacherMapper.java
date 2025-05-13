package pe.getsemani.mikhipu.person.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.person.dto.create.TeacherCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.TeacherResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Teacher;

@Component
public class TeacherMapper {

    private final PersonMapper personMapper;

    public TeacherMapper(PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    public Teacher toEntity(TeacherCreateDTO dto, Person person) {
        return Teacher.builder()
                .code(dto.getCode())
                .person(person)
                .build();
    }

    public TeacherResponseDTO toDto(Teacher teacher) {
        TeacherResponseDTO dto = new TeacherResponseDTO();
        dto.setId(teacher.getId());
        dto.setCode(teacher.getCode());
        dto.setPerson(personMapper.toDto(teacher.getPerson()));  // usa el mapper
        return dto;
    }
}
