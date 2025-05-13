package pe.getsemani.mikhipu.person.dto;

import lombok.Data;

@Data
public class CourseTeacherViewDTO {
    private Long id;
    private String fullName;
    private String code;
    private String role; // PRINCIPAL o AUXILIAR
}
