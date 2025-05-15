package pe.getsemani.mikhipu.persons.teacher.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCreateDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;

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
