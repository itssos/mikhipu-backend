package pe.getsemani.mikhipu.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // "DIRECT" o "COURSE"
    private Long courseId; // Solo si es por curso
    private int toUserId; // Solo si es directo
    private String content;
    private String attachmentUrl; // URL del archivo adjunto, si existe
    private String attachmentName;
    private String attachmentType; // mime-type: "image/png", "application/pdf", etc
    private Long senderId;
    private String senderName;
    private LocalDateTime timestamp;
}
