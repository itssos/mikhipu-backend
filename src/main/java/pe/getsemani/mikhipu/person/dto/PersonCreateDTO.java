package pe.getsemani.mikhipu.person.dto;

import lombok.Data;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;

import java.time.LocalDate;

@Data
public class PersonCreateDTO {
    /**
     * Indica el tipo de persona a crear. Por ejemplo: STUDENT, TEACHER, REPRESENTATIVE.
     */
    private String type;

    // Campos comunes de Person
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String phone;

    // Campos específicos para Student (como ejemplo)
    private Integer grade;
    private String section;
    private String schoolLevel;

    private UserCreateDTO user;
}
