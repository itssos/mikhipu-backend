package pe.getsemani.mikhipu.person.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.PersonDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.repository.UserRepository;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(PersonMapper::mapPersonToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id " + id));
        return PersonMapper.mapPersonToDTO(person);
    }

    @Transactional
    public PersonDTO createPerson(PersonCreateDTO dto) {
        Person person;
        if ("ESTUDIANTE".equalsIgnoreCase(dto.getType())) {
            Student student = new Student();
            // Campos comunes de Person
            student.setFirstName(dto.getFirstName());
            student.setLastName(dto.getLastName());
            student.setDni(dto.getDni());
            student.setBirthDate(dto.getBirthDate());
            student.setGender(dto.getGender());
            student.setAddress(dto.getAddress());
            student.setPhone(dto.getPhone());
            // Campos específicos de Student
            student.setGrade(dto.getGrade());
            student.setSection(Section.valueOf(dto.getSection().toUpperCase()));
            student.setSchoolLevel(SchoolLevel.valueOf(dto.getSchoolLevel().toUpperCase()));
            // Procesar el objeto User enviado en la petición
            if (dto.getUser() != null) {
                UserCreateDTO userDto = dto.getUser();
                User newUser = new User();
                newUser.setUsername(userDto.getUsername() != null ? userDto.getUsername() : dto.getDni());
                newUser.setEmail(userDto.getEmail());
                newUser.setPassword(passwordEncoder.encode(userDto.getPassword() != null ? userDto.getPassword() : dto.getDni()));
                // Si se envían roles, asignarlos convirtiendo de String a Role
                if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                    Set<Role> roles = userDto.getRoles().stream()
                            .map(roleStr -> roleRepository.findByName(RoleType.valueOf(roleStr))
                                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleStr)))
                            .collect(Collectors.toSet());
                    newUser.setRoles(roles);
                }
                student.setUser(newUser);
            } else {
                User newUser = new User();
                newUser.setUsername(dto.getDni());
                newUser.setPassword(passwordEncoder.encode(dto.getDni()));
                student.setUser(newUser);
            }
            person = student;
        } else {
            throw new IllegalArgumentException("Unsupported person type: " + dto.getType());
        }
        Person saved = personRepository.save(person);
        return PersonMapper.mapPersonToDTO(saved);
    }

    @Transactional
    public PersonDTO updatePerson(Long id, PersonCreateDTO dto) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id " + id));
        // Actualización de campos comunes
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setDni(dto.getDni());
        existing.setBirthDate(dto.getBirthDate());
        existing.setGender(dto.getGender());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());

        if (existing instanceof Student && "ESTUDIANTE".equalsIgnoreCase(dto.getType())) {
            Student student = (Student) existing;
            // Actualización de campos específicos de Student
            student.setGrade(dto.getGrade());
            student.setSection(Section.valueOf(dto.getSection().toUpperCase()));
            student.setSchoolLevel(SchoolLevel.valueOf(dto.getSchoolLevel().toUpperCase()));
            // Actualizar o crear el User asociado según la información enviada
            if (dto.getUser() != null) {
                UserCreateDTO userDto = dto.getUser();
                if (student.getUser() != null) {
                    // Actualizar el User existente
                    User userEntity = student.getUser();
                    if (userDto.getUsername() != null) {
                        userEntity.setUsername(userDto.getUsername());
                    }
                    if (userDto.getEmail() != null) {
                        userEntity.setEmail(userDto.getEmail());
                    }
                    if (userDto.getPassword() != null) {
                        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }
                    if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                        Set<Role> roles = userDto.getRoles().stream()
                                .map(roleStr -> roleRepository.findByName(RoleType.valueOf(roleStr))
                                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleStr)))
                                .collect(Collectors.toSet());
                        userEntity.setRoles(roles);
                    }
                } else {
                    // Si no existe el User, crearlo
                    User newUser = new User();
                    newUser.setUsername(userDto.getUsername() != null ? userDto.getUsername() : dto.getDni());
                    newUser.setEmail(userDto.getEmail());
                    newUser.setPassword(passwordEncoder.encode(userDto.getPassword() != null ? userDto.getPassword() : dto.getDni()));
                    if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                        Set<Role> roles = userDto.getRoles().stream()
                                .map(roleStr -> roleRepository.findByName(RoleType.valueOf(roleStr))
                                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleStr)))
                                .collect(Collectors.toSet());
                        newUser.setRoles(roles);
                    }
                    student.setUser(newUser);
                }
            }
        } else if (!"STUDENT".equalsIgnoreCase(dto.getType())) {
            throw new IllegalArgumentException("Unsupported person type for update: " + dto.getType());
        }

        Person updated = personRepository.save(existing);
        return PersonMapper.mapPersonToDTO(updated);
    }

    @Transactional
    public void deletePerson(Long id) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id " + id));
        personRepository.delete(existing);
    }
}
