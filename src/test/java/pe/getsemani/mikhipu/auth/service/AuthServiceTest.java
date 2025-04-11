package pe.getsemani.mikhipu.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import pe.getsemani.mikhipu.security.JwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("authenticate() with valid credentials returns JWT and type")
    void authenticate_withValidCredentials_returnsJwtAuthResponse() {
        // Arrange
        String rawUsername = "john.doe";
        String rawPassword = "s3cr3t";
        LoginRequest request = new LoginRequest();
        request.setUsername(rawUsername);
        request.setPassword(rawPassword);

        var fakeAuth = new UsernamePasswordAuthenticationToken(rawUsername, rawPassword);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(fakeAuth);
        when(tokenProvider.generateToken(fakeAuth))
                .thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");

        // Act
        JwtAuthResponse response = authService.authenticate(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        // Verify that AuthenticationManager and TokenProvider were called correctly
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(rawUsername, rawPassword));
        verify(tokenProvider).generateToken(fakeAuth);
    }

    @Test
    @DisplayName("authenticate() with bad credentials throws 401 Unauthorized")
    void authenticate_withBadCredentials_throwsResponseStatusException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("invalid");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        ResponseStatusException ex = catchThrowableOfType(
                () -> authService.authenticate(request),
                ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Bad credentials");
    }
}
