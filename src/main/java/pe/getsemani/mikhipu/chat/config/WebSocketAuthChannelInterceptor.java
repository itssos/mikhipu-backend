package pe.getsemani.mikhipu.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.security.JwtTokenProvider;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Accede a los headers STOMP
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // Solo para el comando CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && !token.isBlank()) {
                String rawToken = token;
                if (token != null && token.startsWith("Bearer ")) {
                    rawToken = token.substring(7); // Quita "Bearer "
                }
                String username = jwtTokenProvider.getUsername(rawToken);
                if (username != null) {
                    accessor.setUser(new StompPrincipal(username));
                    log.info("WebSocket conectado como: {}", username);
                }
            }
        } else if (accessor.getUser() == null) {
            // Intenta propagar el usuario de la sesión, si existe
            Object user = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("user") : null;
            if (user instanceof Principal principal) {
                accessor.setUser(principal);
            }
        }

        return message;
    }
}
