package pe.getsemani.mikhipu.course.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RemoveTeachersDTO {
    @NotEmpty
    private Set<String> teacherCodes;
}