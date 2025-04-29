package pe.getsemani.mikhipu.person.dto.create;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;

import java.time.LocalDate;

@Data
public class PersonCreateDTO {
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String phone;
    private UserCreateDTO user;
}