package pe.getsemani.mikhipu.persons.person.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;

import java.time.LocalDate;

@Data
public class PersonCreateDTO {

    @Schema(description = "Nombres de la persona", example = "Carlos", required = true)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String firstName;

    @Schema(description = "Apellidos de la persona", example = "Pérez Gómez", required = true)
    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 100, message = "El apellido debe tener como máximo 100 caracteres.")
    private String lastName;

    @Schema(description = "Número de DNI (Documento Nacional de Identidad)", example = "12345678", required = true)
    @NotBlank(message = "El DNI no puede estar vacío.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos.")
    private String dni;

    @Schema(description = "Género de la persona", example = "MASCULINO", required = true, implementation = Gender.class)
    @NotNull(message = "El género no puede estar vacío.")
    private Gender gender;

    @Schema(description = "Dirección de residencia", example = "Av. Siempre Viva 742", required = true)
    @NotBlank(message = "La dirección no puede estar vacía.")
    @Size(max = 150, message = "La dirección debe tener como máximo 150 caracteres.")
    private String address;

    @Schema(description = "Número de teléfono", example = "987654321", required = true)
    @NotBlank(message = "El número de teléfono no puede estar vacío.")
    @Size(min = 9, max = 15, message = "El teléfono debe tener entre 9 y 15 caracteres.")
    private String phone;

    @Schema(description = "Fecha de nacimiento en formato ISO", example = "2000-05-15", required = true)
    @NotNull(message = "La fecha de nacimiento no puede estar vacía.")
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate birthDate;

    @Valid
    @Schema(description = "Datos del usuario asociados a la persona", required = true)
    private UserCreateDTO user;
}