package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.Quarter;

import java.util.Set;

@Data
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer year;
    private Quarter quarter;
}