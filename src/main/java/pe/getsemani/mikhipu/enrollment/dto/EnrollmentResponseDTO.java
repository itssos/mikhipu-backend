package pe.getsemani.mikhipu.enrollment.dto;

import lombok.Data;
import pe.getsemani.mikhipu.enrollment.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentFullName;
    private String year;
    private BigDecimal monthlyFee;
    private BigDecimal enrollmentFee;
    private EnrollmentStatus status;
    private LocalDate enrollmentDate;
}