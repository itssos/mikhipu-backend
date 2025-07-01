package pe.getsemani.mikhipu.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.getsemani.mikhipu.chat.entity.ChatMessage;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Por curso
    List<ChatMessage> findByTypeAndCourseIdOrderByTimestampAsc(String type, Long courseId);
    // Por chat directo (ambos sentidos)
    List<ChatMessage> findByTypeAndSenderIdAndToUserIdOrderByTimestampAsc(String type, Long senderId, int toUserId);
    List<ChatMessage> findByTypeAndSenderIdOrToUserIdOrderByTimestampAsc(String type, Long userId1, int userId2);
}
