package pe.getsemani.mikhipu.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;
import pe.getsemani.mikhipu.chat.repository.ChatMessageRepository;
import pe.getsemani.mikhipu.chat.service.ChatPermissionService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatWebSocketController {
    private final ChatPermissionService permissionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    // Chat directo profesor-estudiante
    @MessageMapping("/chat.direct")
    public void handleDirectMessage(@Payload ChatMessage message, Principal principal) {
        if (!permissionService.canDirectChat(principal.getName(), message.getToUserId())) {
            throw new AccessDeniedException("No tienes permiso");
        }
        message.setTimestamp(LocalDateTime.now());
        message.setType("DIRECT");
        message.setSenderId((long) getUserId(principal.getName())); // asegúrate de setear senderId

        // Guarda el mensaje en la BD
        chatMessageRepository.save(message);

        String toUsername = userRepository.findById(message.getToUserId())
                .map(User::getUsername)
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no existe"));

        // Envía a destinatario
        messagingTemplate.convertAndSendToUser(toUsername, "/queue/messages", message);
        // Envía a remitente (opcional, para refresco inmediato)
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages", message);
    }

    @MessageMapping("/chat.course.{courseId}")
    @SendTo("/topic/course.{courseId}")
    public ChatMessage handleCourseMessage(@DestinationVariable Long courseId, @Payload ChatMessage message, Principal principal) {
        if (!permissionService.isEnrolledInCourse(principal.getName(), courseId)) {
            throw new AccessDeniedException("No tienes permiso");
        }
        message.setTimestamp(LocalDateTime.now());
        message.setType("COURSE");
        message.setCourseId(courseId);
        message.setSenderId((long) getUserId(principal.getName()));
        // Guarda el mensaje en la BD
        chatMessageRepository.save(message);
        return message;
    }

    private int getUserId(String username) {
        return userRepository.findByUsername(username).map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado: " + username));
    }
}