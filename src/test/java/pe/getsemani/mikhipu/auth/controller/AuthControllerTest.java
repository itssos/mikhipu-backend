package pe.getsemani.mikhipu.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.auth.service.AuthService;
import pe.getsemani.mikhipu.exception.GlobalExceptionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/auth/login with valid payload returns 200 and JWT")
    @WithMockUser
    void login_withValidRequest_returns200AndJwt() throws Exception {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("password123");
        JwtAuthResponse fakeResponse = new JwtAuthResponse("token123", "Bearer");

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login with missing username returns 400 and validation message")
    @WithMockUser
    void login_missingUsername_returns400() throws Exception {
        // Arrange: faltante el campo username
        String payload = """
            {
              "password": "password123"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").value("Username is required"));
    }

    @Test
    @DisplayName("POST /api/auth/login with missing password returns 400 and validation message")
    @WithMockUser
    void login_missingPassword_returns400() throws Exception {
        // Arrange: faltante el campo password
        String payload = """
            {
              "username": "alice"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").value("Password is required"));
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid credentials returns 401")
    @WithMockUser
    void login_invalidCredentials_returns401() throws Exception {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setUsername("bob");
        req.setPassword("wrong");
        when(authService.authenticate(any()))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Bad credentials"));
    }
}
