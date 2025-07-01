package pe.getsemani.mikhipu.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import pe.getsemani.mikhipu.security.JwtTokenProvider;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // Aquí intenta obtener el token, si no existe no rompas la conexión
        String token = null;
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // Puedes intentar leer del parámetro (si el frontend lo manda así)
            token = servletRequest.getServletRequest().getParameter("access_token");
            // O intenta de headers personalizados si lo mandas como 'Sec-WebSocket-Protocol' o similar
            // token = servletRequest.getServletRequest().getHeader("Authorization");
        }

        if (token == null || token.isBlank()) {
            // No hay token, no asocias principal (la conexión aún puede funcionar si después el canal lo mete)
            return true;
        }

        String rawToken = token;
        if (token != null && token.startsWith("Bearer ")) {
            rawToken = token.substring(7); // Quita "Bearer "
        }
        String username = jwtTokenProvider.getUsername(rawToken);
        if (username != null) {
            attributes.put("user", new StompPrincipal(username));
        }
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}

}
