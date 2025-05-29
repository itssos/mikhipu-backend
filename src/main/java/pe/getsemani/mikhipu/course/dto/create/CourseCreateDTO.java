package pe.getsemani.mikhipu.course.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.course.enums.Quarter;

@Data
public class CourseCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    @NotNull
    private Integer year;

    @NotNull
    private Quarter quarter;
}