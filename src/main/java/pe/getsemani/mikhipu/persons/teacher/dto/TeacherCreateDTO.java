package pe.getsemani.mikhipu.persons.teacher.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class TeacherCreateDTO {

    @Valid
    @Schema(description = "Datos personales del docente", required = true)
    @NotNull(message = "Los datos de la persona no pueden ser nulos.")
    private PersonCreateDTO person;

    @Schema(description = "Código único del docente", example = "DOC-1234", required = true)
    @NotBlank(message = "El código del docente no puede estar vacío.")
    @Size(min = 4, max = 20, message = "El código del docente debe tener entre 4 y 20 caracteres.")
    private String code;
}
