package pe.getsemani.mikhipu.enrollment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.getsemani.mikhipu.enrollment.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EnrollmentCreateDTO {

    @NotNull
    private Long studentId;

    @NotBlank
    private String year;

    @NotNull
    private BigDecimal enrollmentFee;

    @NotNull
    private BigDecimal monthlyFee;

    @NotNull
    private EnrollmentStatus status;

    @NotNull
    private LocalDate enrollmentDate;
}