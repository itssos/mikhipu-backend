package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonResponseDTO createPerson(PersonCreateDTO dto) {
        Person person = personMapper.fromCreateDto(dto);
        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    public Person saveRaw(Person person) {
        return personRepository.save(person);
    }

    public PersonResponseDTO getPersonById(Long id) {
        Person person = findPersonById(id);
        return personMapper.toDto(person);
    }

    public List<PersonResponseDTO> getAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    protected Person findPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
    }
}
