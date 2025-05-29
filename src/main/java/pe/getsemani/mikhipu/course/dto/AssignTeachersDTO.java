package pe.getsemani.mikhipu.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class AssignTeachersDTO {

    @NotBlank
    private String mainTeacherCode;

    private Set<String> auxiliaryTeacherCodes;
}