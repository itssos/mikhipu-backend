package pe.getsemani.mikhipu.course.dto;

import lombok.Data;

@Data
public class CourseTeacherViewDTO {
    private Long id;
    private int userId;
    private String fullName;
    private String code;
    private String role; // PRINCIPAL o AUXILIAR
}
