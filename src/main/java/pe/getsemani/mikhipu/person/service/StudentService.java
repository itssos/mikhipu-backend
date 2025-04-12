package pe.getsemani.mikhipu.person.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.dto.UploadResponse;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.person.repository.StudentRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          RoleRepository roleRepository,
                          UserRepository userRepository,
                          Validator validator, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Procesa la carga de estudiantes desde un archivo Excel.
     * Se valida cada registro, se crea el usuario si no existe (con username y password = DNI)
     * y se asigna el rol ESTUDIANTE. Si se detecta algún error, se marca la transacción para rollback.
     */
    @Transactional
    public UploadResponse uploadStudentsFromExcel(MultipartFile file) {
        UploadResponse response = new UploadResponse();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // Se asume que la primera fila es el header; se inicia en la fila 1.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    Student student = parseStudentFromRow(row);

                    // Validar campos del estudiante usando Bean Validation
                    Set<ConstraintViolation<Student>> violations = validator.validate(student);
                    if (!violations.isEmpty()){
                        String errorMsg = "Fila " + (i + 1) + " - Errores de validación: " +
                                violations.stream()
                                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                                        .collect(Collectors.joining(", "));
                        errors.add(errorMsg);
                        continue;
                    }

                    // Si el estudiante no tiene usuario asignado, se genera uno.
                    if (student.getUser() == null) {
                        User user = new User();
                        user.setUsername(student.getDni());
                        user.setPassword(passwordEncoder.encode(student.getDni())); // Se recomienda encriptar la contraseña en producción.
                        // Buscar el rol de ESTUDIANTE.
                        Role estudianteRole = roleRepository.findByName(RoleType.ESTUDIANTE)
                                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rol de estudiante para el DNI: " + student.getDni()));
                        if (estudianteRole == null) {
                            errors.add("Fila " + (i + 1) + " - No se encontró el rol de estudiante para el DNI: " + student.getDni());
                            continue;
                        }
                        user.setRoles(Collections.singleton(estudianteRole));
                        student.setUser(user);
                    }

                    studentRepository.save(student);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Fila " + (i + 1) + " - Error al procesar registro: " + e.getMessage());
                }
            }
        } catch (IOException ioe) {
            errors.add("Error al leer el archivo: " + ioe.getMessage());
        }

        response.setSuccessCount(successCount);
        response.setFailureCount(errors.size());
        response.setErrors(errors);

        // Si se detectaron errores, se marca la transacción para rollback.
        if (!errors.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return response;
    }

    /**
     * Procesa la carga de estudiantes a partir de una lista enviada en JSON.
     * Se valida cada registro y se aplica la misma lógica de negocio que el proceso de Excel.
     */
    @Transactional
    public UploadResponse uploadStudentsFromList(List<Student> students) {
        UploadResponse response = new UploadResponse();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Student student : students) {
            try {
                // Validar el objeto Student
                Set<ConstraintViolation<Student>> violations = validator.validate(student);
                if (!violations.isEmpty()){
                    String errorMsg = "Error en el estudiante con DNI " + student.getDni() + ": " +
                            violations.stream()
                                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                                    .collect(Collectors.joining(", "));
                    errors.add(errorMsg);
                    continue;
                }

                if (student.getUser() == null) {
                    User user = new User();
                    user.setUsername(student.getDni());
                    user.setPassword(passwordEncoder.encode(student.getDni()));
                    Role estudianteRole = roleRepository.findByName(RoleType.ESTUDIANTE)
                            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rol de estudiante para el DNI: " + student.getDni()));
                    if (estudianteRole == null) {
                        errors.add("No se encontró el rol de estudiante para el DNI: " + student.getDni());
                        continue;
                    }
                    user.setRoles(Collections.singleton(estudianteRole));
                    student.setUser(user);
                }
                studentRepository.save(student);
                successCount++;
            } catch(Exception e) {
                errors.add("Error al guardar el estudiante con DNI " + student.getDni() + ": " + e.getMessage());
            }
        }
        response.setSuccessCount(successCount);
        response.setFailureCount(errors.size());
        response.setErrors(errors);

        if (!errors.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return response;
    }

    // Método auxiliar para parsear cada fila del Excel y mapearla a un objeto Student.
    // Se asume una estructura de columnas definida (por ejemplo, 0: firstName, 1: lastName, 2: dni, 3: birthDate, 4: gender, 5: address, 6: phone, 7: grade, 8: section, 9: schoolLevel).
    private Student parseStudentFromRow(Row row) {
        Student student = new Student();
        student.setFirstName(getCellValueAsString(row.getCell(0)));
        student.setLastName(getCellValueAsString(row.getCell(1)));
        student.setDni(getCellValueAsString(row.getCell(2)));
        // Se espera que la fecha esté en formato ISO (yyyy-MM-dd) o se puede ajustar según el formato que manejes.
        student.setBirthDate(LocalDate.parse(getCellValueAsString(row.getCell(3))));
        student.setGender(getCellValueAsString(row.getCell(4)));
        student.setAddress(getCellValueAsString(row.getCell(5)));
        student.setPhone(getCellValueAsString(row.getCell(6)));
        student.setGrade(Integer.parseInt(getCellValueAsString(row.getCell(7))));
        student.setSection(Section.valueOf(getCellValueAsString(row.getCell(8))));
        student.setSchoolLevel(SchoolLevel.valueOf(getCellValueAsString(row.getCell(9)).toUpperCase()));
        return student;
    }

    // Método auxiliar para obtener el valor de una celda en formato String.
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch(cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    // Se asume que es número entero, sino ajustar según la necesidad.
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Operaciones CRUD

    public List<Student> listAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró al estudiante con ID: " + id ));
    }

    public Student createStudent(Student student) {
        if (student.getUser() == null) {
            User user = new User();
            user.setUsername(student.getDni());
            user.setPassword(passwordEncoder.encode(student.getDni()));
            Role estudianteRole = roleRepository.findByName(RoleType.ESTUDIANTE)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rol de estudiante para el DNI: " + student.getDni()));
            if (estudianteRole != null) {
                user.setRoles(Collections.singleton(estudianteRole));
            }
            student.setUser(user);
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con id: " + id));
        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setDni(studentDetails.getDni());
        student.setBirthDate(studentDetails.getBirthDate());
        student.setGender(studentDetails.getGender());
        student.setAddress(studentDetails.getAddress());
        student.setPhone(studentDetails.getPhone());
        student.setGrade(studentDetails.getGrade());
        student.setSection(studentDetails.getSection());
        student.setSchoolLevel(studentDetails.getSchoolLevel());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con id: " + id));
        studentRepository.delete(student);
    }
}
