package pe.getsemani.mikhipu.person.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.getsemani.mikhipu.person.dto.UploadResponse;
import pe.getsemani.mikhipu.person.entity.Student;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;
import pe.getsemani.mikhipu.person.service.StudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@WithMockUser(roles = "ADMINISTRADOR")
@DisplayName("Pruebas del controlador de estudiantes")
class StudentControllerTest {

    @MockitoBean
    private StudentService studentService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/students/upload/excel: Carga de archivo Excel")
    void uploadExcel_returnsUploadResponse() throws Exception {
        // Arrange: Se simula un archivo Excel mediante MockMultipartFile
        byte[] content = "dummy content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "students.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, content);
        UploadResponse fakeResponse = new UploadResponse(1, 0, List.of());
        when(studentService.uploadStudentsFromExcel(any())).thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/students/upload/excel")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(0));
    }

    @Test
    @DisplayName("POST /api/students/upload/list: Carga de lista de estudiantes")
    void uploadStudentList_returnsUploadResponse() throws Exception {
        // Arrange: Se crea una lista con un estudiante dummy
        Student student = createDummyStudent();
        List<Student> studentList = Arrays.asList(student);
        UploadResponse fakeResponse = new UploadResponse(1, 0, List.of());
        when(studentService.uploadStudentsFromList(anyList())).thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(post("/api/students/upload/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentList))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(0));
    }

    @Test
    @DisplayName("GET /api/students: Listado de estudiantes")
    void getAllStudents_returnsStudentList() throws Exception {
        // Arrange: Se simula la lista de estudiantes
        Student student = createDummyStudent();
        List<Student> studentList = Arrays.asList(student);
        when(studentService.listAllStudents()).thenReturn(studentList);

        // Act & Assert
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value(student.getFirstName()));
    }

    @Test
    @DisplayName("GET /api/students/{id}: Obtener estudiante por ID")
    void getStudentById_returnsStudent() throws Exception {
        // Arrange
        Student student = createDummyStudent();
        when(studentService.getStudentById(any(Long.class))).thenReturn(student);

        // Act & Assert
        mockMvc.perform(get("/api/students/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()));
    }

    @Test
    @DisplayName("POST /api/students: Crear estudiante")
    void createStudent_returnsCreatedStudent() throws Exception {
        // Arrange
        Student student = createDummyStudent();
        Student createdStudent = createDummyStudent();
        createdStudent.setId(1L);
        when(studentService.createStudent(any(Student.class))).thenReturn(createdStudent);

        // Act & Assert
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()));
    }

    @Test
    @DisplayName("PUT /api/students/{id}: Actualizar estudiante")
    void updateStudent_returnsUpdatedStudent() throws Exception {
        // Arrange
        Student student = createDummyStudent();
        student.setId(1L);
        Student updatedStudent = createDummyStudent();
        updatedStudent.setId(1L);
        updatedStudent.setFirstName("Actualizado");
        when(studentService.updateStudent(eq(1L), any(Student.class))).thenReturn(updatedStudent);

        // Act & Assert
        mockMvc.perform(put("/api/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/students/{id}: Eliminar estudiante")
    void deleteStudent_returnsNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/students/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // Método auxiliar para crear un estudiante dummy
    private Student createDummyStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Juan");
        student.setLastName("Perez");
        student.setDni("12345678");
        student.setBirthDate(LocalDate.of(2005, 5, 1));
        student.setGender("M");
        student.setAddress("Calle 123");
        student.setPhone("987654321");
        student.setGrade(3);
        student.setSection(Section.A);
        student.setSchoolLevel(SchoolLevel.PRIMARIA);
        return student;
    }
}
