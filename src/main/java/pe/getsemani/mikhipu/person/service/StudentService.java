package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.person.dto.create.StudentCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.StudentMapper;
import pe.getsemani.mikhipu.person.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.person.repository.StudentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final PersonService personService;
    private final RepresentativeRepository representativeRepository;

    public StudentResponseDTO createStudent(StudentCreateDTO dto) {
        Student student = StudentMapper.fromCreateDto(dto);

        Person person = PersonMapper.fromCreateDto(dto.getPerson());
        student.setPerson(person);

        if (dto.getRepresentativeIds() != null && !dto.getRepresentativeIds().isEmpty()) {
            Set<Representative> representatives = new HashSet<>(representativeRepository.findAllById(dto.getRepresentativeIds()));
            student.setRepresentatives(representatives);
        }

        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentById(id);
        return StudentMapper.toDto(student);
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    protected Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }
}
