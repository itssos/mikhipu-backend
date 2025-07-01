package pe.getsemani.mikhipu.chat.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;
import pe.getsemani.mikhipu.chat.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test de ChatHistoryController")
class ChatHistoryControllerTest {

    @Mock ChatMessageRepository chatMessageRepository;
    ChatHistoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ChatHistoryController(chatMessageRepository);
    }

    // --------- Unitario ---------
    @Test
    @DisplayName("Unitario: Consulta historial por curso")
    void testGetHistoryByCourse() {
        ChatMessage msg = new ChatMessage();
        msg.setId(1L); msg.setType("COURSE"); msg.setCourseId(100L);
        when(chatMessageRepository.findByTypeAndCourseIdOrderByTimestampAsc("COURSE", 100L))
                .thenReturn(List.of(msg));

        List<ChatMessage> res = controller.getChatHistory("COURSE", 100L, null, null);
        assertEquals(1, res.size());
        assertEquals(100L, res.get(0).getCourseId());
    }

    @Test
    @DisplayName("Unitario: Consulta historial directo entre dos usuarios")
    void testGetHistoryByDirectChat() {
        ChatMessage m1 = new ChatMessage();
        m1.setType("DIRECT"); m1.setSenderId(1L); m1.setToUserId(2);
        m1.setTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0, 1));
        ChatMessage m2 = new ChatMessage();
        m2.setType("DIRECT"); m2.setSenderId(2L); m2.setToUserId(1);
        m2.setTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0, 2));
        when(chatMessageRepository.findAll()).thenReturn(List.of(m1, m2));

        List<ChatMessage> res = controller.getChatHistory("DIRECT", null, 2L, 1L);
        assertEquals(2, res.size());
        assertEquals(m1, res.get(0));
        assertEquals(m2, res.get(1));
    }

    // --------- Caja Negra ---------
    @Nested
    @DisplayName("Caja negra: API /api/chat/history")
    class BlackBoxApi {
        MockMvc mockMvc;
        @BeforeEach
        void setUpMvc() {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        @DisplayName("GET /history?type=COURSE&courseId=10 retorna mensajes del curso")
        void testGetHistoryApiCourse() throws Exception {
            ChatMessage msg = new ChatMessage();
            msg.setId(2L); msg.setType("COURSE"); msg.setCourseId(10L);
            when(chatMessageRepository.findByTypeAndCourseIdOrderByTimestampAsc("COURSE", 10L))
                    .thenReturn(List.of(msg));

            mockMvc.perform(get("/api/chat/history")
                            .param("type", "COURSE")
                            .param("courseId", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(2L));
        }

        @Test
        @DisplayName("GET /history?type=DIRECT&toUserId=2&userId=1 retorna mensajes directos")
        void testGetHistoryApiDirect() throws Exception {
            ChatMessage m1 = new ChatMessage();
            m1.setType("DIRECT"); m1.setSenderId(1L); m1.setToUserId(2);
            m1.setTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0, 1));
            when(chatMessageRepository.findAll()).thenReturn(List.of(m1));
            mockMvc.perform(get("/api/chat/history")
                            .param("type", "DIRECT")
                            .param("userId", "1")
                            .param("toUserId", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].senderId").value(1L));
        }
    }

    // --------- Caja Blanca (ramas y filtros internos) ---------
    @Test
    @DisplayName("Caja blanca: retorna lista vacía si no hay coincidencias")
    void testEmptyResultIfNoMatch() {
        when(chatMessageRepository.findByTypeAndCourseIdOrderByTimestampAsc(any(), any()))
                .thenReturn(List.of());
        List<ChatMessage> res = controller.getChatHistory("COURSE", 999L, null, null);
        assertTrue(res.isEmpty());

        when(chatMessageRepository.findAll()).thenReturn(List.of());
        res = controller.getChatHistory("DIRECT", null, 2L, 1L);
        assertTrue(res.isEmpty());

        // Sin parámetros relevantes
        res = controller.getChatHistory(null, null, null, null);
        assertTrue(res.isEmpty());
    }

    // --------- TDD ---------
    @Test
    @DisplayName("TDD: Debe retornar mensajes ordenados por fecha en chat directo")
    void tddDirectChatOrderedByTimestamp() {
        ChatMessage m1 = new ChatMessage();
        m1.setType("DIRECT"); m1.setSenderId(1L); m1.setToUserId(2);
        m1.setTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0, 2));
        ChatMessage m2 = new ChatMessage();
        m2.setType("DIRECT"); m2.setSenderId(2L); m2.setToUserId(1);
        m2.setTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0, 1));
        when(chatMessageRepository.findAll()).thenReturn(List.of(m1, m2));

        List<ChatMessage> res = controller.getChatHistory("DIRECT", null, 2L, 1L);
        assertEquals(m2, res.get(0)); // más antiguo primero
        assertEquals(m1, res.get(1));
    }
}
