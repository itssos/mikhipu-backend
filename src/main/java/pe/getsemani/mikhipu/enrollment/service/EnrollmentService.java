package pe.getsemani.mikhipu.enrollment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.course.dto.UpdateEnrollmentStatusDTO;
import pe.getsemani.mikhipu.enrollment.dto.EnrollmentCreateDTO;
import pe.getsemani.mikhipu.enrollment.dto.EnrollmentResponseDTO;
import pe.getsemani.mikhipu.enrollment.entity.Enrollment;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.enrollment.mapper.EnrollmentMapper;
import pe.getsemani.mikhipu.enrollment.repository.EnrollmentRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentRepository studentRepository;

    @Transactional
    public EnrollmentResponseDTO createEnrollment(EnrollmentCreateDTO dto) {
        boolean exists = enrollmentRepository.existsByStudent_IdAndYear(dto.getStudentId(), dto.getYear());
        if (exists) {
            throw new IllegalArgumentException("El estudiante ya tiene una matrícula registrada para el año " + dto.getYear());
        }

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .year(dto.getYear())
                .enrollmentFee(dto.getEnrollmentFee())
                .monthlyFee(dto.getMonthlyFee())
                .status(dto.getStatus())
                .enrollmentDate(dto.getEnrollmentDate())
                .build();

        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponseDTO updateEnrollmentStatus(UpdateEnrollmentStatusDTO dto) {
        Enrollment enrollment = enrollmentRepository
                .findByStudent_IdAndYear(dto.getStudentId(), dto.getYear())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula no encontrada para ese estudiante y año"));

        enrollment.setStatus(dto.getStatus());
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }


    @Transactional
    public List<EnrollmentResponseDTO> findAllByStudentId(Long studentId) {
        return enrollmentRepository.findAllByStudentId(studentId).stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

}
