package pe.getsemani.mikhipu.person.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDTO extends PersonDTO{
    private Integer grade;
    private String section;
    private String schoolLevel;
}
