package pe.getsemani.mikhipu.assistance.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.assistance.dto.*;
import pe.getsemani.mikhipu.assistance.entity.*;
import pe.getsemani.mikhipu.assistance.enums.*;
import pe.getsemani.mikhipu.assistance.mapper.*;
import pe.getsemani.mikhipu.assistance.repository.*;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.student.repository.StudentRepository;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests para AssistanceRecordService usando mockito-inline para LocalTime/LocalDate/LocalDateTime.now.
 */
@ExtendWith(MockitoExtension.class)
class AssistanceRecordServiceTest {

    @Mock private AssistanceRecordRepository recordRepository;
    @Mock private AssistanceSessionRepository sessionRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private AssistanceRecordMapper recordMapper;
    @Mock private AssistanceSessionService assistanceSessionService;
    @Mock private AssistanceSessionMapper assistanceSessionMapper;

    @InjectMocks
    private AssistanceRecordService service;

    private AssistanceSession sessionConfig;
    private AssistanceSessionResponseDTO sessionResponseDTO;
    private AssistanceRecordCreateDTO createDTO;
    private AssistanceRecordUpdateDTO updateDTO;
    private AssistanceRecord record;
    private Student student;

    @BeforeEach
    void setup() {
        student = Student.builder().id(1L).build();
        sessionConfig = AssistanceSession.builder()
                .id(1L)
                .startEntryTime(LocalTime.of(8, 0))
                .endEntryTime(LocalTime.of(8, 30))
                .startExitTime(LocalTime.of(13, 0))
                .endExitTime(LocalTime.of(14, 0))
                .attendanceDeadline(LocalDateTime.of(2025, 6, 12, 10, 0))
                .active(true)
                .build();

        sessionResponseDTO = new AssistanceSessionResponseDTO();
        sessionResponseDTO.setStartEntryTime(LocalTime.of(8, 0));
        sessionResponseDTO.setEndEntryTime(LocalTime.of(8, 30));
        sessionResponseDTO.setStartExitTime(LocalTime.of(13, 0));
        sessionResponseDTO.setEndExitTime(LocalTime.of(14, 0));
        sessionResponseDTO.setAttendanceDeadline(LocalDateTime.of(2025, 6, 12, 10, 0));

        createDTO = new AssistanceRecordCreateDTO();
        createDTO.setStudentId(1L);

        updateDTO = new AssistanceRecordUpdateDTO();
        updateDTO.setEntryMarkedAt(LocalDateTime.of(2025, 6, 12, 8, 10));
        updateDTO.setEntryStatus(AssistanceEntryStatus.PRESENTE);
        updateDTO.setExitMarkedAt(LocalDateTime.of(2025, 6, 12, 13, 30));
        updateDTO.setExitStatus(AssistanceExitStatus.SALIDA_REGULAR);
        updateDTO.setEdited(true);

        record = AssistanceRecord.builder()
                .id(1L)
                .student(student)
                .date(LocalDate.of(2025, 6, 12))
                .entryStatus(AssistanceEntryStatus.NO_MARCADA)
                .build();
    }

    // ========================
    // TESTS UNITARIOS
    // ========================

    @Test
    @DisplayName("Unitario: Editar registro después de deadline (throw)")
    void testEditRecordAfterDeadlineThrows() {
        AssistanceSession sessionWithPastDeadline = AssistanceSession.builder()
                .attendanceDeadline(LocalDateTime.now().minusDays(1)).active(true).build();

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(sessionRepository.findByActiveTrue()).thenReturn(Optional.of(sessionWithPastDeadline));

        assertThatThrownBy(() -> service.editRecord(1L, updateDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ya no se puede editar");
    }

    // ========================
    // TESTS DE CAJA BLANCA
    // ========================

    @Test
    @DisplayName("Caja blanca: Verifica asignación de estado entrada 'TARDANZA'")
    void testEntryStatusTardanza() {
        LocalTime time = LocalTime.of(8, 40); // después de endEntry pero antes de startExit
        AssistanceEntryStatus status = service.getEntryStatus(time, sessionConfig);
        assertThat(status).isEqualTo(AssistanceEntryStatus.TARDANZA);
    }

    @Test
    @DisplayName("Caja blanca: Verifica asignación de estado salida 'SALIDA_ANTICIPADA'")
    void testExitStatusAnticipada() {
        LocalTime time = LocalTime.of(12, 30); // antes de startExit
        AssistanceExitStatus status = service.getExitStatus(time, sessionConfig);
        assertThat(status).isEqualTo(AssistanceExitStatus.SALIDA_ANTICIPADA);
    }

    // ========================
    // TESTS DE INTEGRACIÓN (mock paginado y especificaciones)
    // ========================

    @Test
    @DisplayName("Integración: Buscar registros por filtro devuelve Page")
    void testGetRecordsByFilterReturnsPage() {
        AssistanceReportFilterDTO filter = new AssistanceReportFilterDTO();
        Pageable pageable = PageRequest.of(0, 10);
        AssistanceRecord record = AssistanceRecord.builder().id(1L).build();
        when(recordRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(record)));
        when(recordMapper.toResponseDto(any(AssistanceRecord.class)))
                .thenReturn(new AssistanceRecordResponseDTO());

        Page<AssistanceRecordResponseDTO> page = service.getRecordsByFilter(filter, pageable);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(1);
    }

    // ========================
    // TESTS DE CAJA BLANCA: Lógica de cierre y ausencias
    // ========================

    @Test
    @DisplayName("Caja blanca: Marcar ausentes correctamente al cerrar el día")
    void testCloseDayAndMarkAbsences() {
        AssistanceRecord absent = AssistanceRecord.builder()
                .id(2L)
                .entryStatus(AssistanceEntryStatus.NO_MARCADA)
                .build();
        AssistanceRecord presente = AssistanceRecord.builder()
                .id(3L)
                .entryStatus(AssistanceEntryStatus.PRESENTE)
                .build();

        when(recordRepository.findByDate(any(LocalDate.class)))
                .thenReturn(List.of(absent, presente));

        service.closeDayAndMarkAbsences(LocalDate.of(2025, 6, 12));

        assertThat(absent.getEntryStatus()).isEqualTo(AssistanceEntryStatus.AUSENTE);
        assertThat(presente.getEntryStatus()).isEqualTo(AssistanceEntryStatus.PRESENTE);
        verify(recordRepository).saveAll(anyList());
    }
}
