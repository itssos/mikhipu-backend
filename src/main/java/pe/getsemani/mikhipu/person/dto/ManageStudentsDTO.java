package pe.getsemani.mikhipu.person.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ManageStudentsDTO {
    @NotEmpty
    private Set<Long> studentIds;
}