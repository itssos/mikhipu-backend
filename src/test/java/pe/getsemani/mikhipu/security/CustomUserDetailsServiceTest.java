package pe.getsemani.mikhipu.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pe.getsemani.mikhipu.model.entity.User;
import pe.getsemani.mikhipu.model.entity.Role;
import pe.getsemani.mikhipu.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void shouldLoadUserByUsername() {
        User user = mock(User.class);
        Role role = mock(Role.class);
        when(role.getName()).thenReturn("ROLE_USER");
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
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
