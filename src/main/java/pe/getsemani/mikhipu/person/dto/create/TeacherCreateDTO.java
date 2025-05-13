package pe.getsemani.mikhipu.person.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherCreateDTO {

    @NotNull
    private PersonCreateDTO person;

    @NotBlank
    private String code;
}