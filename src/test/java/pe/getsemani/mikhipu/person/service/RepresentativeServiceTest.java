package pe.getsemani.mikhipu.person.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.RepresentativeMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.person.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.person.repository.StudentRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepresentativeServiceTest {

    @Mock
    private RepresentativeRepository representativeRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private RepresentativeMapper representativeMapper;
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private RepresentativeService representativeService;

    private RepresentativeCreateDTO createDTO;
    private Representative representative;
    private Person person;
    private RepresentativeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        person = new Person();
        representative = new Representative();
        responseDTO = new RepresentativeResponseDTO();

        createDTO = new RepresentativeCreateDTO();
        PersonCreateDTO personDTO = new PersonCreateDTO();
        createDTO.setPerson(personDTO);
        createDTO.setStudentIds(Set.of(1L, 2L));
    }

    @Test
    @DisplayName("Debe crear un representante correctamente con estudiantes")
    void createRepresentative_success() {
        when(representativeMapper.fromCreateDto(createDTO)).thenReturn(representative);
        when(personMapper.fromCreateDto(createDTO.getPerson())).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);
        when(studentRepository.findAllById(createDTO.getStudentIds())).thenReturn(List.of(new Student(), new Student()));
        when(representativeRepository.save(representative)).thenReturn(representative);
        when(representativeMapper.toDto(representative)).thenReturn(responseDTO);

        RepresentativeResponseDTO result = representativeService.createRepresentative(createDTO);

        assertNotNull(result);
        verify(personRepository).save(person);
        verify(studentRepository).findAllById(createDTO.getStudentIds());
        verify(representativeRepository).save(representative);
    }

    @Test
    @DisplayName("Debe obtener un representante por ID exitosamente")
    void getRepresentativeById_success() {
        when(representativeRepository.findById(1L)).thenReturn(Optional.of(representative));
        when(representativeMapper.toDto(representative)).thenReturn(responseDTO);

        RepresentativeResponseDTO result = representativeService.getRepresentativeById(1L);

        assertNotNull(result);
        verify(representativeRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el ID del representante no existe")
    void getRepresentativeById_notFound() {
        when(representativeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                representativeService.getRepresentativeById(99L)
        );
    }

    @Test
    @DisplayName("Debe retornar todos los representantes")
    void getAllRepresentatives_success() {
        List<Representative> mockList = List.of(new Representative(), new Representative());
        when(representativeRepository.findAll()).thenReturn(mockList);
        when(representativeMapper.toDto(any())).thenReturn(responseDTO);

        List<RepresentativeResponseDTO> result = representativeService.getAllRepresentatives();

        assertEquals(2, result.size());
        verify(representativeRepository).findAll();
    }

    @Test
    @DisplayName("Debe eliminar un representante por ID correctamente")
    void deleteRepresentative_success() {
        when(representativeRepository.existsById(1L)).thenReturn(true);

        representativeService.deleteRepresentative(1L);

        verify(representativeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion si se elimina representante que no existe")
    void deleteRepresentative_notFound() {
        when(representativeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                representativeService.deleteRepresentative(99L)
        );
    }
}
