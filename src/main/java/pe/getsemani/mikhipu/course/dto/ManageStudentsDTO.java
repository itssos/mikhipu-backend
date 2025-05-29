package pe.getsemani.mikhipu.course.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ManageStudentsDTO {
    @NotEmpty
    private Set<Long> studentIds;
}