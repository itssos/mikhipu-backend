package pe.getsemani.mikhipu.person.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class ManageRepresentativesDTO {

    @NotNull
    private Set<Long> representativeIds;
}