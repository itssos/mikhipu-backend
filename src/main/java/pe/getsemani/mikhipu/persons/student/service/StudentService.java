package pe.getsemani.mikhipu.persons.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import pe.getsemani.mikhipu.course.dto.UploadResponse;
import pe.getsemani.mikhipu.persons.representative.dto.RepresentativeBasicDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentCreateDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentCourseViewDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentFilterDTO;
import pe.getsemani.mikhipu.persons.student.dto.StudentResponseDTO;
import pe.getsemani.mikhipu.persons.person.dto.PersonCreateDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.representative.entity.Representative;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.person.enums.Gender;
import pe.getsemani.mikhipu.persons.student.enums.SchoolLevel;
import pe.getsemani.mikhipu.persons.student.enums.Section;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.student.mapper.StudentMapper;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.persons.representative.repository.RepresentativeRepository;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepresentativeRepository;
import pe.getsemani.mikhipu.persons.person.service.PersonService;
import pe.getsemani.mikhipu.persons.student.specification.StudentSpecification;
import pe.getsemani.mikhipu.user.entity.User;
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
    private final PersonMapper personMapper;
    private final StudentMapper studentMapper;
    private final StudentRepresentativeRepository studentRepresentativeRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public StudentResponseDTO createStudent(StudentCreateDTO dto) {
        // Mapear DTO a entidad
        Student student = studentMapper.fromCreateDto(dto);

        // Mapear y guardar Person
        Person person = personMapper.fromCreateDto(dto.getPerson());
        Person savedPerson = personService.saveRaw(person);
        student.setPerson(savedPerson);

        // Asignar representantes si hay IDs
        if (dto.getRepresentativeIds() != null && !dto.getRepresentativeIds().isEmpty()) {
            Set<Representative> reps = new HashSet<>(
                    representativeRepository.findAllById(dto.getRepresentativeIds())
            );
            student.setRepresentatives(reps);
        }

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentCreateDTO dto) {
        Student student = findStudentById(id);

        // Actualizar datos de Person
        Person person = student.getPerson();
        var pDto = dto.getPerson();
        person.setFirstName(pDto.getFirstName());
        person.setLastName(pDto.getLastName());
        person.setDni(pDto.getDni());
        person.setBirthDate(pDto.getBirthDate());
        person.setGender(pDto.getGender());
        person.setAddress(pDto.getAddress());
        person.setPhone(pDto.getPhone());
        personService.saveRaw(person);

        // Actualizar campos específicos de Student
        student.setGrade(dto.getGrade());
        student.setSection(dto.getSection());
        student.setSchoolLevel(dto.getSchoolLevel());

        // Reemplazar representantes sin modificar la colección activa
        Set<Representative> newReps = new HashSet<>();
        if (dto.getRepresentativeIds() != null && !dto.getRepresentativeIds().isEmpty()) {
            newReps = new HashSet<>(representativeRepository.findAllById(dto.getRepresentativeIds()));
        }
        student.setRepresentatives(newReps);

        Student updated = studentRepository.save(student);
        return studentMapper.toDto(updated);
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentById(id);
        return studentMapper.toDto(student);
    }

    public String getStudentFirstNameById(Long id){
        return findStudentById(id).getPerson().getFirstName();
    }

    public String getStudentLastNameById(Long id){
        return findStudentById(id).getPerson().getLastName();
    }

    public List<StudentCourseViewDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toCourseViewDto)
                .collect(Collectors.toList());
    }

    public Page<StudentCourseViewDTO> getAllStudentsFiltered(StudentFilterDTO filter, Pageable pageable) {
        return studentRepository.findAll(StudentSpecification.build(filter), pageable)
                .map(student -> {
                    StudentCourseViewDTO dto = new StudentCourseViewDTO();
                    dto.setId(student.getId());
                    dto.setFullName(student.getPerson().getFirstName() + " " + student.getPerson().getLastName());
                    dto.setDni(student.getPerson().getDni());
                    dto.setGrade(student.getGrade());
                    dto.setSection(student.getSection());
                    dto.setSchoolLevel(student.getSchoolLevel());
                    return dto;
                });
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Person person = student.getPerson();
        User user = person.getUser();

        studentRepository.delete(student);
        if (user != null) {
            userRepository.delete(user);
        }
        personRepository.delete(person);
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
        var personDto = new PersonCreateDTO();

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

    @Transactional
    public void assignRepresentativesToStudent(Long studentId, Set<Long> representativeIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        Set<Representative> representatives = new HashSet<>(representativeRepository.findAllById(representativeIds));

        student.getRepresentatives().addAll(representatives);
        studentRepository.save(student);
    }

    @Transactional
    public void removeRepresentativesFromStudent(Long studentId, Set<Long> representativeIds) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        studentRepresentativeRepository.removeRepresentativesFromStudent(studentId, representativeIds);
    }

    public List<RepresentativeBasicDTO> getRepresentativesByStudentId(Long studentId) {
        return studentRepository.findRepresentativesByStudentId(studentId).stream()
                .map(proj -> {
                    RepresentativeBasicDTO dto = new RepresentativeBasicDTO();
                    dto.setId(proj.getId());
                    dto.setFullName(proj.getFullName());
                    return dto;
                })
                .toList();
    }


}
