package pe.getsemani.mikhipu.scores.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;
import pe.getsemani.mikhipu.scores.dto.*;
import pe.getsemani.mikhipu.scores.service.ScoreService;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ScoreControllerTest {

    @InjectMocks
    private ScoreController scoreController;

    @Mock
    private ScoreService scoreService;

    @Mock
    private StudentRepository studentRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(scoreController).build();
    }

    // === CAJA NEGRA: registro de nota por POST ===
    @Test
    @DisplayName("Registrar nota - flujo exitoso")
    void registerScore_success() {
        ScoreCreateDTO dto = new ScoreCreateDTO();
        ScoreResponseDTO responseDTO = new ScoreResponseDTO();
        when(scoreService.registerScore(dto)).thenReturn(responseDTO);

        ResponseEntity<ScoreResponseDTO> response = scoreController.registerScore(dto);
        assertEquals(responseDTO, response.getBody(), "Debe retornar el DTO esperado");
    }

    // === CAJA BLANCA: consultar mis notas ===
    @Test
    @DisplayName("Consultar mis notas: obtiene studentId y llama service con filters")
    void getMyScores_returnsNotasForStudent() {
        // Arrange
        ScoreFilterDTOMe filter = new ScoreFilterDTOMe();
        Pageable pageable = PageRequest.of(0, 5);

        Student student = new Student();
        student.setId(9L);
        when(studentRepository.findByPerson_User_Username("estudiante"))
                .thenReturn(Optional.of(student));
        Page<ScoreResponseDTO> fakePage = new PageImpl<>(java.util.List.of());
        when(scoreService.findScoresByStudentWithFilters(9L, filter, pageable))
                .thenReturn(fakePage);

        // Simula contexto de seguridad
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("estudiante");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        ResponseEntity<Page<ScoreResponseDTO>> response = scoreController.getMyScores(filter, pageable);

        // Assert
        assertEquals(fakePage, response.getBody());
    }

    // === CAJA NEGRA: notas de hijos ===
    @Test
    @DisplayName("Consultar notas de mis hijos - flujo exitoso")
    void getMyChildrenScores_success() {
        ScoreFilterDTOMe filter = new ScoreFilterDTOMe();
        Pageable pageable = PageRequest.of(0, 2);

        Page<RepresentativeScoreHistoryDTO> resultPage = new PageImpl<>(java.util.List.of());
        when(scoreService.findScoresOfMyChildren("apoderado", filter, pageable)).thenReturn(resultPage);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("apoderado");
        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<Page<RepresentativeScoreHistoryDTO>> response = scoreController.getMyChildrenScores(filter, pageable);
        assertEquals(resultPage, response.getBody());
    }

    // === CAJA NEGRA/BLANCA: historial de notas, acceso por roles ===
    @Test
    @DisplayName("Consultar historial de notas - devuelve paginado y filtra por username")
    void getHistory_success() {
        ScoreHistoryFilterDTO filter = new ScoreHistoryFilterDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ScoreResponseDTO> expectedPage = new PageImpl<>(java.util.List.of());

        when(scoreService.getHistory(filter, pageable, "usuarioX")).thenReturn(expectedPage);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuarioX");

        ResponseEntity<Page<ScoreResponseDTO>> response = scoreController.getHistory(filter, pageable, authentication);
        assertEquals(expectedPage, response.getBody());
    }

    // === UNITARIO/TDD: promedio ponderado ===
    @Test
    @DisplayName("Consultar promedio ponderado - calcula correctamente")
    void getWeightedAverage_success() {
        when(scoreService.getWeightedAverage(1L, 2L, "2025", "PRIMER")).thenReturn(18.7);

        ResponseEntity<Double> response = scoreController.getWeightedAverage(1L, 2L, "2025", "PRIMER");
        assertEquals(18.7, response.getBody());
    }
}
