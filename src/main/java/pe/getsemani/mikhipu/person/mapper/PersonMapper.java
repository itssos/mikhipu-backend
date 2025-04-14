package pe.getsemani.mikhipu.person.mapper;

import java.util.stream.Collectors;
import pe.getsemani.mikhipu.person.dto.PersonDTO;
import pe.getsemani.mikhipu.person.dto.StudentDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.user.dto.UserDTO;

public class PersonMapper {

    public static PersonDTO mapPersonToDTO(Person person) {
        if (person instanceof Student) {
            Student student = (Student) person;
            StudentDTO dto = new StudentDTO();
            // Mapear campos comunes de Person
            dto.setId(student.getId());
            dto.setFirstName(student.getFirstName());
            dto.setLastName(student.getLastName());
            dto.setDni(student.getDni());
            dto.setBirthDate(student.getBirthDate());
            dto.setGender(student.getGender());
            dto.setAddress(student.getAddress());
            dto.setPhone(student.getPhone());
            // Mapear campos específicos de Student
            dto.setGrade(student.getGrade());
            dto.setSection(student.getSection().toString());
            dto.setSchoolLevel(student.getSchoolLevel().toString());
            // Mapear el User asociado, si existe
            if (student.getUser() != null) {
                UserDTO userDto = new UserDTO();
                userDto.setId(student.getUser().getId());
                userDto.setUsername(student.getUser().getUsername());
                userDto.setEmail(student.getUser().getEmail());
                userDto.setRoles(student.getUser().getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toSet()));
                dto.setUser(userDto);
            }
            return dto;
        } else {
            // Mapeo para otras subclases de Person (solamente los campos comunes)
            PersonDTO dto = new PersonDTO();
            dto.setId(person.getId());
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setDni(person.getDni());
            dto.setBirthDate(person.getBirthDate());
            dto.setGender(person.getGender());
            dto.setAddress(person.getAddress());
            dto.setPhone(person.getPhone());
            if (person.getUser() != null) {
                UserDTO userDto = new UserDTO();
                userDto.setId(person.getUser().getId());
                userDto.setUsername(person.getUser().getUsername());
                userDto.setEmail(person.getUser().getEmail());
                userDto.setRoles(person.getUser().getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toSet()));
                dto.setUser(userDto);
            }
            return dto;
        }
    }
}
