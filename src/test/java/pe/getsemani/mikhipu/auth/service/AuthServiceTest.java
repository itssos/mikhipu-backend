package pe.getsemani.mikhipu.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.security.JwtTokenProvider;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas unitarias del servicio AuthService")
class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserRepository userRepository;
    private PersonRepository personRepository;
    private RoleRepository roleRepository;
    private PersonMapper personMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        tokenProvider = mock(JwtTokenProvider.class);
        userRepository = mock(UserRepository.class);
        personRepository = mock(PersonRepository.class);
        roleRepository = mock(RoleRepository.class);
        personMapper = mock(PersonMapper.class);

        authService = new AuthService(
                authenticationManager, tokenProvider, userRepository,
                personRepository, roleRepository, personMapper
        );
    }

    @Test
    @DisplayName("Debe autenticar exitosamente y retornar token y persona")
    void autenticarConExito() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("usuario");
        loginRequest.setPassword("clave");

        User user = new User();
        user.setUsername("usuario");

        Person person = new Person();
        PersonResponseDTO personDto = new PersonResponseDTO();

        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(personRepository.findByUserUsername("usuario")).thenReturn(Optional.of(person));
        when(personMapper.toDto(person)).thenReturn(personDto);
        when(tokenProvider.generateToken(any())).thenReturn("token.jwt");

        // Act
        JwtAuthResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("token.jwt", response.getToken()); // acceso directo al campo
        assertEquals("Bearer", response.getTokenType());
        assertEquals(personDto, response.getPerson());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void autenticarUsuarioNoEncontrado() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("noexiste");
        loginRequest.setPassword("clave");

        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.authenticate(loginRequest));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("User not found"));
    }

    @Test
    @DisplayName("Debe autenticar aunque no haya persona asociada")
    void autenticarSinPersona() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("usuario");
        loginRequest.setPassword("clave");

        User user = new User();
        user.setUsername("usuario");

        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(personRepository.findByUserUsername("usuario")).thenReturn(Optional.empty());
        when(tokenProvider.generateToken(any())).thenReturn("token.jwt");

        JwtAuthResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("token.jwt", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertNull(response.getPerson());
    }
}
