package pe.getsemani.mikhipu.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class ManageRepresentativesDTO {

    @NotNull
    private Set<Long> representativeIds;
}