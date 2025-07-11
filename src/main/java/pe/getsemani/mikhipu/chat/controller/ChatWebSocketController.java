package pe.getsemani.mikhipu.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;
import pe.getsemani.mikhipu.chat.repository.ChatMessageRepository;
import pe.getsemani.mikhipu.chat.service.ChatPermissionService;
import pe.getsemani.mikhipu.notifications.entity.PushSubscription;
import pe.getsemani.mikhipu.notifications.repository.PushSubscriptionRepository;
import pe.getsemani.mikhipu.notifications.service.PushNotificationService;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

// ---- Agrega tus imports de notificaciones push:
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatWebSocketController {
    private final ChatPermissionService permissionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    // ---- AUTOWIRED para push notifications
    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;
    @Autowired
    private PushNotificationService pushNotificationService;

    // Chat directo profesor-estudiante
    @MessageMapping("/chat.direct")
    public void handleDirectMessage(@Payload ChatMessage message, Principal principal) throws Exception {
        if (!permissionService.canDirectChat(principal.getName(), message.getToUserId())) {
            throw new AccessDeniedException("No tienes permiso");
        }
        message.setTimestamp(LocalDateTime.now());
        message.setType("DIRECT");
        message.setSenderId((long) getUserId(principal.getName())); // set senderId

        // --- Cambia el ORDEN: primero verifica que el destinatario existe
        String toUsername = userRepository.findById(message.getToUserId())
                .map(User::getUsername)
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no existe"));

        // Ahora sí: guarda el mensaje
        chatMessageRepository.save(message);

        // Envía a destinatario
        messagingTemplate.convertAndSendToUser(toUsername, "/queue/messages", message);
        // Envía a remitente (opcional, para refresco inmediato)
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages", message);

        // ---- Notificación push para el destinatario
        List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(message.getToUserId());
        String notificationPayload = buildNotificationPayload(message, "Nuevo mensaje");
        pushNotificationService.sendPushToUser(subs, notificationPayload);
    }

    @MessageMapping("/chat.course.{courseId}")
    @SendTo("/topic/course.{courseId}")
    public ChatMessage handleCourseMessage(@DestinationVariable Long courseId, @Payload ChatMessage message, Principal principal) throws Exception {
        if (!permissionService.isEnrolledInCourse(principal.getName(), courseId)) {
            throw new AccessDeniedException("No tienes permiso");
        }
        message.setTimestamp(LocalDateTime.now());
        message.setType("COURSE");
        message.setCourseId(courseId);
        message.setSenderId((long) getUserId(principal.getName()));

        // Guarda el mensaje en la BD
        chatMessageRepository.save(message);

        // ---- Notificación push a TODOS los usuarios inscritos en el curso (menos el remitente)
        List<User> enrolledUsers = permissionService.getUsersOfCourse(courseId);
        for (User user : enrolledUsers) {
            System.out.println("Evaluando usuario para notificación: " + user.getId() + " - " + user.getUsername());
            if (!message.getSenderId().equals(user.getId().longValue())) {
                System.out.println("Enviando push a: " + user.getId());
                List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(user.getId());
                String notificationPayload = buildNotificationPayload(message , "Nuevo mensaje de curso");
                pushNotificationService.sendPushToUser(subs, notificationPayload);
            } else {
                System.out.println("NO se envía push (es el remitente): " + user.getId());
            }
        }


        return message;
    }

    private int getUserId(String username) {
        return userRepository.findByUsername(username).map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado: " + username));
    }

    public String buildNotificationPayload(ChatMessage message, String titulo) throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("title", titulo);
        payload.put("msg", message.getContent());
        payload.put("body", "Tienes un nuevo mensaje de " + message.getSenderName());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(payload);
    }
}
