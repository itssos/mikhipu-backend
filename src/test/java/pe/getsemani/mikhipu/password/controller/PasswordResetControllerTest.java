package pe.getsemani.mikhipu.password.controller;

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
import pe.getsemani.mikhipu.exception.GlobalExceptionHandler;
import pe.getsemani.mikhipu.password.dto.ForgotPasswordRequest;
import pe.getsemani.mikhipu.password.dto.ResetPasswordRequest;
import pe.getsemani.mikhipu.password.service.PasswordResetService;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Pruebas del controlador de restablecimiento de contraseña")
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/auth/forgot-password con email válido retorna 200")
    @WithMockUser
    void forgotPassword_withValidEmail_returns200() throws Exception {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("user@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("If an account with that email exists, a password reset link has been sent."));

        verify(passwordResetService).createPasswordResetToken("user@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password sin email retorna 400")
    @WithMockUser
    void forgotPassword_missingEmail_returns400() throws Exception {
        String payload = "{}";

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value("Email is required"));
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password con email inválido retorna 400")
    @WithMockUser
    void forgotPassword_invalidEmail_returns400() throws Exception {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("not-an-email");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value("Email should be valid"));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password con payload válido retorna 200")
    @WithMockUser
    void resetPassword_withValidRequest_returns200() throws Exception {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("tok123");
        req.setNewPassword("newPassword");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been successfully reset."));

        verify(passwordResetService).resetPassword("tok123", "newPassword");
    }

    @Test
    @DisplayName("POST /api/auth/reset-password sin token retorna 400")
    @WithMockUser
    void resetPassword_missingToken_returns400() throws Exception {
        String payload = """
            {
              "newPassword": "newPassword"
            }
            """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.token").value("Token is required"));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password sin contraseña retorna 400")
    @WithMockUser
    void resetPassword_missingPassword_returns400() throws Exception {
        String payload = """
            {
              "token": "tok123"
            }
            """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.newPassword").value("New password is required"));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password con contraseña corta retorna 400")
    @WithMockUser
    void resetPassword_shortPassword_returns400() throws Exception {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("tok123");
        req.setNewPassword("short");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.newPassword").value("Password must be at least 8 characters"));
    }
}
