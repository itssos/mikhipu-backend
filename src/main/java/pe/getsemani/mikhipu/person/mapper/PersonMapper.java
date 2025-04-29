package pe.getsemani.mikhipu.person.mapper;

import pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.user.mapper.UserMapper;

public class PersonMapper {

    public static PersonResponseDTO toDto(Person person) {
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
        dto.setUser(UserMapper.toDto(person.getUser()));
        return dto;
    }

    public static Person fromCreateDto(PersonCreateDTO dto) {
        if (dto == null) return null;

        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setDni(dto.getDni());
        person.setBirthDate(dto.getBirthDate());
        person.setGender(dto.getGender());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        person.setUser(UserMapper.fromCreateDto(dto.getUser()));
        return person;
    }
}