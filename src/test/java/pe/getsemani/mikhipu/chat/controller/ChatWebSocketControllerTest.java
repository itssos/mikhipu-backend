package pe.getsemani.mikhipu.chat.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;
import pe.getsemani.mikhipu.chat.repository.ChatMessageRepository;
import pe.getsemani.mikhipu.chat.service.ChatPermissionService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test de ChatWebSocketController - Unitario, Caja negra, Blanca y TDD")
class ChatWebSocketControllerTest {

    @Mock ChatPermissionService permissionService;
    @Mock SimpMessagingTemplate messagingTemplate;
    @Mock UserRepository userRepository;
    @Mock ChatMessageRepository chatMessageRepository;

    ChatWebSocketController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ChatWebSocketController(
                permissionService, messagingTemplate, userRepository, chatMessageRepository
        );
    }

    // ---------- Mock Helper -----------
    Principal mockPrincipal(String name) {
        return () -> name;
    }

    // --------- Unitario: Mensaje directo ---------
    @Test
    @DisplayName("Unitario: handleDirectMessage - éxito")
    void testHandleDirectMessageSuccess() {
        // Arrange
        Principal principal = mockPrincipal("profe");
        ChatMessage msg = new ChatMessage();
        msg.setToUserId(2);

        when(permissionService.canDirectChat("profe", 2)).thenReturn(true);
        when(userRepository.findByUsername("profe")).thenReturn(Optional.of(new User() {{ setId(1); setUsername("profe"); }}));
        when(userRepository.findById(2)).thenReturn(Optional.of(new User() {{ setId(2); setUsername("alumno"); }}));

        // Act
        controller.handleDirectMessage(msg, principal);

        // Assert
        assertEquals("DIRECT", msg.getType());
        assertEquals(1L, msg.getSenderId());
        assertNotNull(msg.getTimestamp());
        verify(chatMessageRepository).save(msg);
        verify(messagingTemplate).convertAndSendToUser(eq("alumno"), eq("/queue/messages"), eq(msg));
        verify(messagingTemplate).convertAndSendToUser(eq("profe"), eq("/queue/messages"), eq(msg));
    }

    // --------- Caja Negra: Autorización ---------
    @Test
    @DisplayName("Caja negra: handleDirectMessage - sin permiso")
    void testHandleDirectMessageDenied() {
        Principal principal = mockPrincipal("userx");
        ChatMessage msg = new ChatMessage();
        msg.setToUserId(4);
        when(permissionService.canDirectChat("userx", 4)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> controller.handleDirectMessage(msg, principal));
        verify(chatMessageRepository, never()).save(any());
    }

    // --------- Caja Blanca: Errores destino/remitente ---------
    @Test
    @DisplayName("Caja blanca: handleDirectMessage - usuario destino no existe")
    void testHandleDirectMessageToUserNotFound() {
        Principal principal = mockPrincipal("profe");
        ChatMessage msg = new ChatMessage();
        msg.setToUserId(99);

        when(permissionService.canDirectChat("profe", 99)).thenReturn(true);
        when(userRepository.findByUsername("profe")).thenReturn(Optional.of(new User() {{ setId(1); setUsername("profe"); }}));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> controller.handleDirectMessage(msg, principal));
        verify(chatMessageRepository, never()).save(any());
    }

    // --------- Unitario: Mensaje curso ---------
    @Test
    @DisplayName("Unitario: handleCourseMessage - éxito")
    void testHandleCourseMessageSuccess() {
        Principal principal = mockPrincipal("alumno");
        ChatMessage msg = new ChatMessage();
        Long courseId = 77L;

        when(permissionService.isEnrolledInCourse("alumno", courseId)).thenReturn(true);
        when(userRepository.findByUsername("alumno")).thenReturn(Optional.of(new User() {{ setId(10); setUsername("alumno"); }}));

        ChatMessage result = controller.handleCourseMessage(courseId, msg, principal);

        assertEquals("COURSE", msg.getType());
        assertEquals(courseId, msg.getCourseId());
        assertEquals(10L, msg.getSenderId());
        assertNotNull(msg.getTimestamp());
        verify(chatMessageRepository).save(msg);
        assertSame(msg, result); // El mensaje devuelto es el mismo que guardó
    }

    // --------- Caja Negra: No autorizado curso ---------
    @Test
    @DisplayName("Caja negra: handleCourseMessage - no inscrito")
    void testHandleCourseMessageDenied() {
        Principal principal = mockPrincipal("noauth");
        ChatMessage msg = new ChatMessage();
        Long courseId = 12L;
        when(permissionService.isEnrolledInCourse("noauth", courseId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> controller.handleCourseMessage(courseId, msg, principal));
        verify(chatMessageRepository, never()).save(any());
    }

    // --------- Caja Blanca: remitente no encontrado ---------
    @Test
    @DisplayName("Caja blanca: handleCourseMessage - remitente no encontrado")
    void testHandleCourseMessageRemitenteNoExiste() {
        Principal principal = mockPrincipal("fantasma");
        ChatMessage msg = new ChatMessage();
        Long courseId = 99L;
        when(permissionService.isEnrolledInCourse("fantasma", courseId)).thenReturn(true);
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> controller.handleCourseMessage(courseId, msg, principal));
        verify(chatMessageRepository, never()).save(any());
    }

    // --------- TDD ---------
    @Test
    @DisplayName("TDD: Todo mensaje directo debe ser guardado y enviado a ambos usuarios")
    void tddDirectMessageGuardaYNotifica() {
        Principal principal = mockPrincipal("tdduser");
        ChatMessage msg = new ChatMessage();
        msg.setToUserId(8);

        when(permissionService.canDirectChat("tdduser", 8)).thenReturn(true);
        when(userRepository.findByUsername("tdduser")).thenReturn(Optional.of(new User() {{ setId(11); setUsername("tdduser"); }}));
        when(userRepository.findById(8)).thenReturn(Optional.of(new User() {{ setId(8); setUsername("destinatario"); }}));

        controller.handleDirectMessage(msg, principal);

        // TDD: Asegura que el mensaje fue guardado y enviado a ambos usuarios
        verify(chatMessageRepository).save(msg);
        verify(messagingTemplate).convertAndSendToUser("destinatario", "/queue/messages", msg);
        verify(messagingTemplate).convertAndSendToUser("tdduser", "/queue/messages", msg);
    }
}
