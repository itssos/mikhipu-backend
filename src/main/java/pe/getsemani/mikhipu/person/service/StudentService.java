package pe.getsemani.mikhipu.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import pe.getsemani.mikhipu.person.dto.UploadResponse;
import pe.getsemani.mikhipu.person.dto.create.StudentCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.StudentResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.entity.Representative;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.mapper.StudentMapper;
import pe.getsemani.mikhipu.person.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.person.repository.StudentRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final RepresentativeRepository representativeRepository;
    private final PersonService personService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentResponseDTO createStudent(StudentCreateDTO dto) {
        Student student = StudentMapper.fromCreateDto(dto);

        Person person = PersonMapper.fromCreateDto(dto.getPerson());
        Person savedPerson = personService.saveRaw(person);
        student.setPerson(savedPerson);

        if (dto.getRepresentativeIds() != null && !dto.getRepresentativeIds().isEmpty()) {
            Set<Representative> representatives = new HashSet<>(representativeRepository.findAllById(dto.getRepresentativeIds()));
            student.setRepresentatives(representatives);
        }

        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentById(id);
        return StudentMapper.toDto(student);
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    protected Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    // Procesa carga masiva desde Lista JSON
    public UploadResponse uploadStudentsFromList(List<StudentCreateDTO> students) {
        UploadResponse response = new UploadResponse();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (StudentCreateDTO dto : students) {
            try {
                createStudent(dto);
                successCount++;
            } catch (Exception e) {
                errors.add("Error en estudiante DNI " + dto.getPerson().getDni() + ": " + e.getMessage());
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

    // Procesa carga masiva desde Excel
    public UploadResponse uploadStudentsFromExcel(MultipartFile file) {
        UploadResponse response = new UploadResponse();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    StudentCreateDTO dto = parseStudentDtoFromRow(row);
                    createStudent(dto);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("Error al leer el archivo: " + e.getMessage());
        }

        response.setSuccessCount(successCount);
        response.setFailureCount(errors.size());
        response.setErrors(errors);

        if (!errors.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return response;
    }

    private StudentCreateDTO parseStudentDtoFromRow(Row row) {
        StudentCreateDTO dto = new StudentCreateDTO();
        var personDto = new pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO();

        personDto.setFirstName(getCellValue(row.getCell(0)));
        personDto.setLastName(getCellValue(row.getCell(1)));
        personDto.setDni(getCellValue(row.getCell(2)));
        personDto.setBirthDate(LocalDate.parse(getCellValue(row.getCell(3))));
        personDto.setGender(Gender.valueOf(getCellValue(row.getCell(4))));
        personDto.setAddress(getCellValue(row.getCell(5)));
        personDto.setPhone(getCellValue(row.getCell(6)));

        dto.setPerson(personDto);
        dto.setGrade(Integer.parseInt(getCellValue(row.getCell(7))));
        dto.setSection(Section.valueOf(getCellValue(row.getCell(8))));
        dto.setSchoolLevel(SchoolLevel.valueOf(getCellValue(row.getCell(9)).toUpperCase()));

        return dto;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
