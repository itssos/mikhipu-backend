package pe.getsemani.mikhipu.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;
import pe.getsemani.mikhipu.chat.repository.ChatMessageRepository;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {
    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long toUserId,
            @RequestParam(required = false) Long userId // puedes mejorar para chats directos
    ) {
        if ("COURSE".equalsIgnoreCase(type) && courseId != null) {
            return chatMessageRepository.findByTypeAndCourseIdOrderByTimestampAsc("COURSE", courseId);
        } else if ("DIRECT".equalsIgnoreCase(type) && toUserId != null && userId != null) {
            // Traer los mensajes entre ambos (ida y vuelta)
            return chatMessageRepository
                    .findAll() // Mejorar: crea un método custom para ambos sentidos entre userId y toUserId
                    .stream()
                    .filter(m -> "DIRECT".equals(m.getType()) &&
                            ((m.getSenderId().equals(userId) && m.getToUserId() == toUserId)
                                    || (m.getSenderId() == toUserId && m.getToUserId() == userId)))
                    .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                    .toList();
        }
        return List.of();
    }
}
