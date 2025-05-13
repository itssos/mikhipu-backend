package pe.getsemani.mikhipu.person.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    private PersonCreateDTO createDTO;
    private Person person;
    private PersonResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = new PersonCreateDTO();
        person = new Person();
        person.setId(1L);
        responseDTO = new PersonResponseDTO();
    }

    @Test
    @DisplayName("Debe crear una persona correctamente")
    void shouldCreatePersonSuccessfully() {
        when(personMapper.fromCreateDto(createDTO)).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);
        when(personMapper.toDto(person)).thenReturn(responseDTO);

        PersonResponseDTO result = personService.createPerson(createDTO);

        assertThat(result).isNotNull();
        verify(personMapper).fromCreateDto(createDTO);
        verify(personRepository).save(person);
        verify(personMapper).toDto(person);
    }

    @Test
    @DisplayName("Debe guardar directamente una persona (raw)")
    void shouldSaveRawPersonSuccessfully() {
        when(personRepository.save(person)).thenReturn(person);

        Person result = personService.saveRaw(person);

        assertThat(result).isEqualTo(person);
        verify(personRepository).save(person);
    }

    @Test
    @DisplayName("Debe devolver una persona por su ID si existe")
    void shouldReturnPersonById() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personMapper.toDto(person)).thenReturn(responseDTO);

        PersonResponseDTO result = personService.getPersonById(1L);

        assertThat(result).isEqualTo(responseDTO);
        verify(personRepository).findById(1L);
        verify(personMapper).toDto(person);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la persona no existe por ID")
    void shouldThrowExceptionWhenPersonNotFound() {
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> personService.getPersonById(999L));

        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Person not found with id: 999");
        verify(personRepository).findById(999L);
        verifyNoInteractions(personMapper);
    }

    @Test
    @DisplayName("Debe devolver todas las personas registradas")
    void shouldReturnAllPersons() {
        List<Person> persons = Arrays.asList(new Person(), new Person());
        List<PersonResponseDTO> dtos = Arrays.asList(new PersonResponseDTO(), new PersonResponseDTO());

        when(personRepository.findAll()).thenReturn(persons);
        when(personMapper.toDto(any(Person.class)))
                .thenReturn(dtos.get(0), dtos.get(1));

        List<PersonResponseDTO> result = personService.getAllPersons();

        assertThat(result).hasSize(2);
        verify(personRepository).findAll();
        verify(personMapper, times(2)).toDto(any(Person.class));
    }

    @Test
    @DisplayName("Debe eliminar una persona por ID sin errores")
    void shouldDeletePerson() {
        doNothing().when(personRepository).deleteById(1L);

        personService.deletePerson(1L);

        verify(personRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una persona inexistente")
    void shouldThrowExceptionWhenDeleteFails() {
        doThrow(new EmptyResultDataAccessException(1)).when(personRepository).deleteById(404L);

        assertThatThrownBy(() -> personService.deletePerson(404L))
                .isInstanceOf(EmptyResultDataAccessException.class);

        verify(personRepository).deleteById(404L);
    }
}
