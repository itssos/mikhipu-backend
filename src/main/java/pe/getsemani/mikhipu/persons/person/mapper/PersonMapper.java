package pe.getsemani.mikhipu.persons.person.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.user.mapper.UserMapper;

@Component
public class PersonMapper {

    private final UserMapper userMapper;

    @Autowired
    public PersonMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PersonResponseDTO toDto(Person person) {
        if (person == null) return null;

        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setDni(person.getDni());
        dto.setBirthDate(person.getBirthDate());
        dto.setGender(person.getGender());
        dto.setAddress(person.getAddress());
        dto.setPhone(person.getPhone());
        dto.setUser(userMapper.toDto(person.getUser()));
        return dto;
    }

    public Person fromCreateDto(PersonCreateDTO dto) {
        if (dto == null) return null;

        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setDni(dto.getDni());
        person.setBirthDate(dto.getBirthDate());
        person.setGender(dto.getGender());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        person.setUser(userMapper.fromCreateDto(dto.getUser()));
        return person;
    }
}
