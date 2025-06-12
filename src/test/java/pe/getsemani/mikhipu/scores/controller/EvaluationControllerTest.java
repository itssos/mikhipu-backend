package pe.getsemani.mikhipu.scores.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pe.getsemani.mikhipu.scores.dto.*;
import pe.getsemani.mikhipu.scores.service.EvaluationService;
import pe.getsemani.mikhipu.persons.teacher.repository.TeacherRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationControllerTest {

    @InjectMocks
    private EvaluationController evaluationController;

    @Mock
    private EvaluationService evaluationService;

    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // === TDD, CAJA NEGRA: Crear evaluación ===
    @Test
    @DisplayName("Crear evaluación (POST) debe delegar en el service y devolver el DTO")
    void createEvaluation_success() {
        EvaluationCreateDTO createDTO = new EvaluationCreateDTO();
        EvaluationResponseDTO responseDTO = new EvaluationResponseDTO();

        when(evaluationService.create(createDTO)).thenReturn(responseDTO);

        ResponseEntity<EvaluationResponseDTO> response = evaluationController.create(createDTO);

        assertEquals(responseDTO, response.getBody());
    }

    // === CAJA BLANCA: Editar evaluación verifica usuario autenticado ===
    @Test
    @DisplayName("Editar evaluación llama al service con username autenticado")
    void editEvaluation_success() {
        EvaluationCreateDTO editDTO = new EvaluationCreateDTO();
        EvaluationResponseDTO responseDTO = new EvaluationResponseDTO();

        // Simular usuario autenticado
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("docente");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(evaluationService.edit(22L, editDTO, "docente")).thenReturn(responseDTO);

        ResponseEntity<EvaluationResponseDTO> response = evaluationController.edit(22L, editDTO);

        assertEquals(responseDTO, response.getBody());
        // No verifica lógica de permisos, solo delegación y username correcto
    }

    // === CAJA BLANCA: Eliminar evaluación verifica usuario autenticado ===
    @Test
    @DisplayName("Eliminar evaluación delega correctamente en el service con username")
    void deleteEvaluation_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        SecurityContextHolder.getContext().setAuthentication(auth);

        doNothing().when(evaluationService).delete(10L, "admin");

        ResponseEntity<Void> response = evaluationController.delete(10L);
        assertEquals(204, response.getStatusCodeValue());
    }

    // === CAJA NEGRA: Filtro de evaluaciones paginadas y por usuario ===
    @Test
    @DisplayName("Filtrar evaluaciones paginado delega en el service con username")
    void filterEvaluations_success() {
        EvaluationFilterDTO filter = new EvaluationFilterDTO();
        Pageable pageable = PageRequest.of(0, 2);

        Page<EvaluationResponseDTO> resultPage = new PageImpl<>(java.util.List.of());

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("profesor");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(evaluationService.filterEvaluations(filter, pageable, "profesor")).thenReturn(resultPage);

        ResponseEntity<Page<EvaluationResponseDTO>> response = evaluationController.filterEvaluations(filter, pageable);

        assertEquals(resultPage, response.getBody());
    }

    // === UNITARIO/TDD: Obtener detalles por ID ===
    @Test
    @DisplayName("getDetails debe devolver el detalle de la evaluación")
    void getDetails_success() {
        EvaluationResponseDTO dto = new EvaluationResponseDTO();
        when(evaluationService.getDetails(15L)).thenReturn(dto);

        ResponseEntity<EvaluationResponseDTO> response = evaluationController.getDetails(15L);

        assertEquals(dto, response.getBody());
    }
}
