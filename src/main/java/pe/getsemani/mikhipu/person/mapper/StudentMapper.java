package pe.getsemani.mikhipu.person.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.person.dto.basic.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.person.dto.create.StudentCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentCourseViewDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    private final PersonMapper personMapper;

    @Autowired
    public StudentMapper(PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    public StudentResponseDTO toDto(Student student) {
        if (student == null) return null;

        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setPerson(personMapper.toDto(student.getPerson()));
        dto.setGrade(student.getGrade());
        dto.setSection(student.getSection());
        dto.setSchoolLevel(student.getSchoolLevel());

        return dto;
    }

    public RepresentativeBasicDTO toBasicRepresentative(Representative representative) {
        if (representative == null) return null;
        RepresentativeBasicDTO dto = new RepresentativeBasicDTO();
        dto.setId(representative.getId());
        dto.setFullName(
                representative.getPerson().getFirstName() + " " + representative.getPerson().getLastName()
        );
        return dto;
    }

    public Student fromCreateDto(StudentCreateDTO dto) {
        if (dto == null) return null;

        Student student = new Student();
        student.setPerson(personMapper.fromCreateDto(dto.getPerson()));
        student.setGrade(dto.getGrade());
        student.setSection(dto.getSection());
        student.setSchoolLevel(dto.getSchoolLevel());
        return student;
    }
    public StudentCourseViewDTO toCourseViewDto(Student student) {
        if (student == null) return null;

        StudentCourseViewDTO dto = new StudentCourseViewDTO();
        dto.setId(student.getId());
        dto.setFullName(
                student.getPerson().getFirstName() + " " + student.getPerson().getLastName()
        );
        dto.setDni(student.getPerson().getDni());
        dto.setGrade(student.getGrade());
        dto.setSection(student.getSection());
        dto.setSchoolLevel(student.getSchoolLevel());

        return dto;
    }
}
