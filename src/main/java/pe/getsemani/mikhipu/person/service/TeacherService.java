package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.person.dto.create.TeacherCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.dto.response.TeacherResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.entity.Teacher;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.StudentMapper;
import pe.getsemani.mikhipu.person.mapper.TeacherMapper;
import pe.getsemani.mikhipu.person.repository.CourseRepository;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.person.repository.TeacherRepository;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PersonRepository personRepository;
    private final TeacherMapper teacherMapper;
    private final PersonMapper personMapper;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    public TeacherResponseDTO create(TeacherCreateDTO dto) {
        String documentNumber = dto.getPerson().getDni();
        Person person = personRepository.findByDni(documentNumber)
                .orElseGet(() -> {
                    Person newPerson = personMapper.fromCreateDto(dto.getPerson());
                    return personRepository.save(newPerson);
                });

        // Evitar duplicado por code único en Teacher
        if (teacherRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("El código del docente ya está en uso");
        }

        Teacher teacher = teacherMapper.toEntity(dto, person);
        teacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(teacher);
    }


    public List<TeacherResponseDTO> findAll() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    public TeacherResponseDTO update(Long id, TeacherCreateDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));

        Person person = teacher.getPerson();
        person.setFirstName(dto.getPerson().getFirstName());
        person.setLastName(dto.getPerson().getLastName());
        person.setDni(dto.getPerson().getDni());

        // Actualiza el User asociado también
        if (person.getUser() != null && dto.getPerson().getUser() != null) {
            String newUsername = dto.getPerson().getUser().getUsername();
            String currentUsername = person.getUser().getUsername();

            if (!newUsername.equals(currentUsername)) {
                // Verifica si otro usuario ya tiene ese username
                if (userRepository.existsByUsername(newUsername)) {
                    throw new IllegalArgumentException("El nombre de usuario ya está en uso");
                }
            }

            person.getUser().setUsername(newUsername);
            person.getUser().setEmail(dto.getPerson().getUser().getEmail());
            person.getUser().setPassword(dto.getPerson().getUser().getPassword());
            person.getUser().setRole(roleService.getRoleByName(dto.getPerson().getUser().getRole()));
        }

        personRepository.save(person);

        teacher.setCode(dto.getCode());
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }


    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
        teacherRepository.delete(teacher);
        personRepository.delete(teacher.getPerson());
        userRepository.delete(teacher.getPerson().getUser());
    }

    public TeacherResponseDTO findById(Long id) {
        return teacherRepository.findById(id)
                .map(teacherMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
    }

    public List<StudentResponseDTO> getStudentsTaughtByTeacher(Long teacherId) {
        List<Student> students = courseRepository.findStudentsByTeacherId(teacherId);
        return students.stream()
                .map(studentMapper::toDto)
                .toList();
    }

}