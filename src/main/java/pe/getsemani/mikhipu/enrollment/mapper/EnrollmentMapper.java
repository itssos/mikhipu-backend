package pe.getsemani.mikhipu.enrollment.mapper;

import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.enrollment.dto.EnrollmentResponseDTO;
import pe.getsemani.mikhipu.enrollment.entity.Enrollment;

@Component
public class EnrollmentMapper {

    public EnrollmentResponseDTO toDto(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentFullName(
                enrollment.getStudent().getPerson().getFirstName() + " " +
                        enrollment.getStudent().getPerson().getLastName()
        );
        dto.setYear(enrollment.getYear());
        dto.setMonthlyFee(enrollment.getMonthlyFee());
        dto.setEnrollmentFee(enrollment.getEnrollmentFee());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        return dto;
    }
}