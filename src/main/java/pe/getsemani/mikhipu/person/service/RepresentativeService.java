package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.RepresentativeMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.person.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.person.repository.StudentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RepresentativeService {

    private final RepresentativeRepository representativeRepository;
    private final StudentRepository studentRepository;
    private final PersonMapper personMapper;
    private final RepresentativeMapper representativeMapper;
    private final PersonRepository personRepository;

    public RepresentativeResponseDTO createRepresentative(RepresentativeCreateDTO dto) {
        Representative representative = representativeMapper.fromCreateDto(dto);

        Person person = personMapper.fromCreateDto(dto.getPerson());
        person = personRepository.save(person); // 🔥 persistir primero
        representative.setPerson(person);

        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            Set<Student> students = new HashSet<>(studentRepository.findAllById(dto.getStudentIds()));
            representative.setStudents(students);
        }

        Representative saved = representativeRepository.save(representative);
        return representativeMapper.toDto(saved);
    }


    public RepresentativeResponseDTO getRepresentativeById(Long id) {
        Representative rep = representativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Representative not found with id: " + id));
        return representativeMapper.toDto(rep);
    }

    public List<RepresentativeResponseDTO> getAllRepresentatives() {
        return representativeRepository.findAll()
                .stream()
                .map(representativeMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRepresentative(Long id) {
        if (!representativeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Representative not found with id: " + id);
        }
        representativeRepository.deleteById(id);
    }
}
