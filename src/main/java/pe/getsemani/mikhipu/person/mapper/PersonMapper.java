package pe.getsemani.mikhipu.person.mapper;

import pe.getsemani.mikhipu.person.dto.PersonDTO;
import pe.getsemani.mikhipu.person.dto.StudentDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Student;

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
            return dto;
        } else {
            // Si es otra subclase de Person, se mapean solo los campos comunes
            PersonDTO dto = new PersonDTO();
            dto.setId(person.getId());
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setDni(person.getDni());
            dto.setBirthDate(person.getBirthDate());
            dto.setGender(person.getGender());
            dto.setAddress(person.getAddress());
            dto.setPhone(person.getPhone());
            return dto;
        }
    }
}
