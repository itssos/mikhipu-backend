package pe.getsemani.mikhipu.person.mapper;

import pe.getsemani.mikhipu.person.dto.basic.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.person.dto.create.StudentCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;

import java.util.Set;
import java.util.stream.Collectors;

public class StudentMapper {

    public static StudentResponseDTO toDto(Student student) {
        if (student == null) return null;

        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setPerson(PersonMapper.toDto(student.getPerson()));
        dto.setGrade(student.getGrade());
        dto.setSection(student.getSection());
        dto.setSchoolLevel(student.getSchoolLevel());

        if (student.getRepresentatives() != null) {
            Set<RepresentativeBasicDTO> reps = student.getRepresentatives()
                    .stream()
                    .map(StudentMapper::toBasicRepresentative)
                    .collect(Collectors.toSet());
            dto.setRepresentatives(reps);
        }

        return dto;
    }

    public static RepresentativeBasicDTO toBasicRepresentative(Representative representative) {
        RepresentativeBasicDTO dto = new RepresentativeBasicDTO();
        dto.setId(representative.getId());
        dto.setFullName(representative.getPerson().getFirstName() + " " + representative.getPerson().getLastName());
        return dto;
    }

    public static Student fromCreateDto(StudentCreateDTO dto) {
        if (dto == null) return null;

        Student student = new Student();
        student.setPerson(PersonMapper.fromCreateDto(dto.getPerson()));
        student.setGrade(dto.getGrade());
        student.setSection(dto.getSection());
        student.setSchoolLevel(dto.getSchoolLevel());
        // El set de representatives se maneja aparte, porque necesitas cargar las entidades Representative antes
        return student;
    }
}
