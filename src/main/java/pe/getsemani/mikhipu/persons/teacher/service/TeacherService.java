package pe.getsemani.mikhipu.persons.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherCreateDTO;
import pe.getsemani.mikhipu.persons.teacher.dto.TeacherResponseDTO;
import pe.getsemani.mikhipu.persons.teacher.entity.Teacher;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.course.repository.CourseRepository;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.persons.teacher.mapper.TeacherMapper;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PersonRepository personRepository;
    private final TeacherMapper teacherMapper;
    private final PersonMapper personMapper;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    /**
     * Crea un nuevo docente con validación de duplicados y mapeo completo de persona y usuario.
     */
    public TeacherResponseDTO create(TeacherCreateDTO dto) {
        String documentNumber = dto.getPerson().getDni();

        // Validar que no exista una persona con el mismo DNI
        if (personRepository.findByDni(documentNumber).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona registrada con el DNI: " + documentNumber);
        }

        // Validar que no exista un usuario con el mismo username
        String username = dto.getPerson().getUser().getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre de usuario: " + username);
        }

        // Asignar el rol DOCENTE de forma explícita
        dto.getPerson().getUser().setRole("DOCENTE");

        // Mapear DTO a entidad y guardar la persona
        Person personEntity = personMapper.fromCreateDto(dto.getPerson());
        personEntity = personRepository.save(personEntity);

        // Validar que el código del docente no esté duplicado
        if (teacherRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("El código del docente ya está en uso.");
        }

        // Crear y guardar el docente
        Teacher teacher = teacherMapper.toEntity(dto, personEntity);
        teacher = teacherRepository.save(teacher);

        return teacherMapper.toDto(teacher);
    }

    /**
     * Lista todos los docentes registrados.
     */
    public List<TeacherResponseDTO> findAll() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza datos del docente y su persona asociada. Valida unicidad de username si se modifica.
     */
    public TeacherResponseDTO update(Long id, TeacherCreateDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));

        Person person = teacher.getPerson();
        person.setFirstName(dto.getPerson().getFirstName());
        person.setLastName(dto.getPerson().getLastName());
        person.setDni(dto.getPerson().getDni());

        if (person.getUser() != null && dto.getPerson().getUser() != null) {
            String newUsername = dto.getPerson().getUser().getUsername();

            // Validar si se cambia el username y que no esté duplicado
            boolean usernameChanged = !newUsername.equals(person.getUser().getUsername());
            if (usernameChanged && userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
            }

            person.getUser().setUsername(newUsername);
            person.getUser().setEmail(dto.getPerson().getUser().getEmail());
            person.getUser().setPassword(dto.getPerson().getUser().getPassword());

            // Forzar rol "DOCENTE"
            person.getUser().setRole(roleService.getRoleByName("DOCENTE"));
        }

        // Validar código de docente si cambió
        String newCode = dto.getCode();
        if (!newCode.equals(teacher.getCode()) && teacherRepository.existsByCode(newCode)) {
            throw new IllegalArgumentException("El código del docente ya está en uso.");
        }

        teacher.setCode(newCode);

        personRepository.save(person);
        teacher = teacherRepository.save(teacher);

        return teacherMapper.toDto(teacher);
    }

    /**
     * Elimina un docente junto con su persona y usuario asociados.
     */
    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));

        // Primero elimina el Teacher para romper la relación con Person
        teacherRepository.delete(teacher);

        // Luego elimina la Person, lo que eliminará también su User gracias al cascade
        personRepository.delete(teacher.getPerson());
    }

    /**
     * Busca un docente por su ID y retorna su DTO correspondiente.
     */
    public TeacherResponseDTO findById(Long id) {
        return teacherRepository.findById(id)
                .map(teacherMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));
    }

    /**
     * Obtiene los estudiantes asignados a cursos impartidos por un docente específico.
     */
    public List<StudentResponseDTO> getStudentsTaughtByTeacher(Long teacherId) {
        List<Student> students = courseRepository.findStudentsByTeacherId(teacherId);
        return students.stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }
}
