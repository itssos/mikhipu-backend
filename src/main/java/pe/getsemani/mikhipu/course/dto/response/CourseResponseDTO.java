package pe.getsemani.mikhipu.course.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.course.enums.Quarter;

@Data
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer year;
    private Quarter quarter;
}