package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;

@Data
public class TeacherResponseDTO {
    private Long id;
    private String code;
    private PersonResponseDTO person;
}