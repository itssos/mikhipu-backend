package pe.getsemani.mikhipu.person.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.person.enums.Quarter;

import java.util.Set;

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