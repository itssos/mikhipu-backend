package pe.getsemani.mikhipu.persons.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentFilterDTO {
    @Schema(description = "DNI del estudiante")
    private String dni;

    @Schema(description = "Nombre completo (puede ser parcial)")
    private String name;

    @Schema(description = "Grado")
    private Integer grade;

    @Schema(description = "Sección (ejemplo: A)")
    private String section;

    @Schema(description = "Nivel escolar")
    private String schoolLevel;

    @Schema(description = "ID del curso")
    private Long courseId;
}
