package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.RepresentativeMapper;
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
    private final PersonService personService;
    private final StudentRepository studentRepository;

    public RepresentativeResponseDTO createRepresentative(RepresentativeCreateDTO dto) {
        Representative representative = RepresentativeMapper.fromCreateDto(dto);

        Person person = PersonMapper.fromCreateDto(dto.getPerson());
        representative.setPerson(person);

        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            Set<Student> students = new HashSet<>(studentRepository.findAllById(dto.getStudentIds()));
            representative.setStudents(students);
        }

        Representative savedRepresentative = representativeRepository.save(representative);
        return RepresentativeMapper.toDto(savedRepresentative);
    }

    public RepresentativeResponseDTO getRepresentativeById(Long id) {
        Representative representative = findRepresentativeById(id);
        return RepresentativeMapper.toDto(representative);
    }

    public List<RepresentativeResponseDTO> getAllRepresentatives() {
        return representativeRepository.findAll()
                .stream()
                .map(RepresentativeMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRepresentative(Long id) {
        representativeRepository.deleteById(id);
    }

    protected Representative findRepresentativeById(Long id) {
        return representativeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representative not found with id: " + id));
    }
}