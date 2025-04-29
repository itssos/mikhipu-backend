package pe.getsemani.mikhipu.person.dto.response;

import lombok.Data;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;

import java.time.LocalDate;

@Data
public class PersonResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String phone;
    private UserResponseDTO user;
}
