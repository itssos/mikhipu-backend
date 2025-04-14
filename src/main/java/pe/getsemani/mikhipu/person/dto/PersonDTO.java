package pe.getsemani.mikhipu.person.dto;

import lombok.Data;
import pe.getsemani.mikhipu.user.dto.UserDTO;

import java.time.LocalDate;

@Data
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String phone;

    private UserDTO user;
}