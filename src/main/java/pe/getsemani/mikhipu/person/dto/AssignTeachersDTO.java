package pe.getsemani.mikhipu.person.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class AssignTeachersDTO {

    @NotBlank
    private String mainTeacherCode;

    private Set<String> auxiliaryTeacherCodes;
}