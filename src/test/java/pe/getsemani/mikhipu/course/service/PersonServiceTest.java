package pe.getsemani.mikhipu.course.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.persons.person.service.PersonService;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.repository.UserRepository;

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
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private PersonService personService;

    private PersonCreateDTO createDTO;
    private Person person;
    private PersonResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Crear un PersonCreateDTO con user no nulo para evitar NPE
        createDTO = new PersonCreateDTO();
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("user");
        createDTO.setUser(userCreateDTO);

        person = new Person();
        person.setId(1L);

        responseDTO = new PersonResponseDTO();

        // Mock RoleService para cualquier rol solicitado
        Role mockRole = new Role();
        mockRole.setId(1);
        mockRole.setName("ROLE_USER");

        // Mock para cualquier string y también para null
        when(roleService.getRoleByName(anyString())).thenReturn(mockRole);
        when(roleService.getRoleByName(null)).thenReturn(mockRole);
        when(roleService.getRoleByName(any())).thenReturn(mockRole);

        // Mock UserRepository
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
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

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Persona no encontrada con ID: 999");
        verify(personRepository).findById(999L);
        verifyNoInteractions(personMapper);
    }

    @Test
    @DisplayName("Debe eliminar una persona por ID sin errores")
    void shouldDeletePerson() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        doNothing().when(personRepository).delete(person);

        personService.deletePerson(1L);

        verify(personRepository).findById(1L);
        verify(personRepository).delete(person);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una persona inexistente")
    void shouldThrowExceptionWhenDeleteFails() {
        when(personRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.deletePerson(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Persona no encontrada con ID: 404");

        verify(personRepository).findById(404L);
        verify(personRepository, never()).deleteById(anyLong());
    }
}