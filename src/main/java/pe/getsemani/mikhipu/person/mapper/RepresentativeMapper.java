package pe.getsemani.mikhipu.person.mapper;

import pe.getsemani.mikhipu.person.dto.basic.StudentBasicDTO;
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;

import java.util.Set;
import java.util.stream.Collectors;

public class RepresentativeMapper {

    public static RepresentativeResponseDTO toDto(Representative representative) {
        if (representative == null) return null;

        RepresentativeResponseDTO dto = new RepresentativeResponseDTO();
        dto.setId(representative.getId());
        dto.setPerson(PersonMapper.toDto(representative.getPerson()));
        dto.setRelationship(representative.getRelationship());

        if (representative.getStudents() != null) {
            Set<StudentBasicDTO> students = representative.getStudents()
                    .stream()
                    .map(RepresentativeMapper::toBasicStudent)
                    .collect(Collectors.toSet());
            dto.setStudents(students);
        }

        return dto;
    }

    public static StudentBasicDTO toBasicStudent(Student student) {
        StudentBasicDTO dto = new StudentBasicDTO();
        dto.setId(student.getId());
        dto.setFullName(student.getPerson().getFirstName() + " " + student.getPerson().getLastName());
        return dto;
    }

    public static Representative fromCreateDto(RepresentativeCreateDTO dto) {
        if (dto == null) return null;

        Representative representative = new Representative();
        representative.setPerson(PersonMapper.fromCreateDto(dto.getPerson()));
        representative.setRelationship(dto.getRelationship());
        // El set de students se maneja aparte, porque necesitas cargar las entidades Student antes
        return representative;
    }
}