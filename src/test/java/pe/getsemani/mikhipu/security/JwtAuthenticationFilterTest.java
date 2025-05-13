package pe.getsemani.mikhipu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Debe autenticar correctamente con un token válido")
    void autenticarConTokenValido() throws Exception {
        String token = "token.valido";
        String username = "usuario";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUsername(token)).thenReturn(username);

        UserDetails userDetails = new User(username, "password", List.of());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(tokenProvider).validateToken(token);
        verify(tokenProvider).getUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Debe retornar 401 si el token ha expirado")
    void tokenExpiradoRetorna401() throws Exception {
        String token = "token.expirado";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expirado"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DelegatingServletOutputStream servletOutputStream = new DelegatingServletOutputStream(baos);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        when(request.getRequestURI()).thenReturn("/api/test");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        String responseBody = baos.toString();
        assertTrue(responseBody.contains("Token expirado"));
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("Debe retornar 401 si la firma del token es inválida")
    void firmaInvalidaRetorna401() throws Exception {
        String token = "token.invalido";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenThrow(new SignatureException("Firma inválida"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DelegatingServletOutputStream servletOutputStream = new DelegatingServletOutputStream(baos);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        when(request.getRequestURI()).thenReturn("/api/test");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        String responseBody = baos.toString();
        assertTrue(responseBody.contains("The JWT token signature is invalid"));
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("Debe continuar la cadena si no hay cabecera Authorization")
    void sinTokenContinuaCadena() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenProvider);
        verifyNoInteractions(userDetailsService);
    }
}
