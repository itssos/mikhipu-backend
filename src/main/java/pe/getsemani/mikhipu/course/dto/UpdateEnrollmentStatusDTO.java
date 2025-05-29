package pe.getsemani.mikhipu.course.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.enrollment.enums.EnrollmentStatus;

@Data
public class UpdateEnrollmentStatusDTO {

    @NotNull
    private Long studentId;

    @NotBlank
    private String year;

    @NotNull
    private EnrollmentStatus status;
}