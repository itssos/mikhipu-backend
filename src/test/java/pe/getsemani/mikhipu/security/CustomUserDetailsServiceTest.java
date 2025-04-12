package pe.getsemani.mikhipu.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    @DisplayName("Debe cargar el usuario por nombre de usuario correctamente")
    void shouldLoadUserByUsername() {
        User user = mock(User.class);
        Role role = mock(Role.class);
        when(role.getName()).thenReturn(RoleType.ESTUDIANTE);
        when(user.getUsername()).thenReturn("user");
        when(user.getPassword()).thenReturn("pass");
        when(user.getRoles()).thenReturn(Collections.singleton(role));
        when(user.isActive()).thenReturn(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("user");

        assertThat(details.getUsername()).isEqualTo("user");
        assertThat(details.getPassword()).isEqualTo("pass");
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no se encuentra el usuario")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
