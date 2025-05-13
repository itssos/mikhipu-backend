package pe.getsemani.mikhipu.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debe cargar detalles del usuario con sus roles y permisos")
    void debeCargarDetallesDeUsuario() {
        // Arrange
        Permission permiso1 = new Permission();
        permiso1.setId(1);
        permiso1.setName("PERMISO_VER");

        Role rol = new Role();
        rol.setId(1);
        rol.setName("ADMIN");
        rol.setPermissions(Set.of(permiso1));

        User user = new User();
        user.setId(1);
        user.setUsername("usuario1");
        user.setPassword("secreta");
        user.setRole(rol);
        user.setActive(true);

        when(userRepository.findByUsername("usuario1")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario1");

        // Assert
        assertEquals("usuario1", userDetails.getUsername());
        assertEquals("secreta", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("PERMISO_VER")));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        when(userRepository.findByUsername("no_existe")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("no_existe"));

        assertEquals("Usuario no encontrado: no_existe", ex.getMessage());
    }
}
