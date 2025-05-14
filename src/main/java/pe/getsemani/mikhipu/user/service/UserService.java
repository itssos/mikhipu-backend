package pe.getsemani.mikhipu.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.mapper.UserMapper;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(UserCreateDTO dto) {
        validateNewUser(dto);

        User user = userMapper.fromCreateDto(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role role = getRoleByName(dto.getRole());
        user.setRole(role);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return userMapper.toDto(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUser(Integer id, UserCreateDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        validateUserUpdate(dto, existingUser);

        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role role = getRoleByName(dto.getRole());
        existingUser.setRole(role);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    // ========================
    // Validaciones y utilitarios
    // ========================

    private void validateNewUser(UserCreateDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalStateException("Ya existe un usuario con el nombre: " + dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() &&
                userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("Ya existe un usuario con el correo: " + dto.getEmail());
        }
    }

    private void validateUserUpdate(UserCreateDTO dto, User existingUser) {
        if (!existingUser.getUsername().equals(dto.getUsername())) {
            Optional<User> byUsername = userRepository.findByUsername(dto.getUsername());
            if (byUsername.isPresent() && !byUsername.get().getId().equals(existingUser.getId())) {
                throw new IllegalStateException("El nombre de usuario ya está en uso: " + dto.getUsername());
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equals(existingUser.getEmail())) {
            Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
            if (byEmail.isPresent() && !byEmail.get().getId().equals(existingUser.getId())) {
                throw new IllegalStateException("El correo ya está en uso: " + dto.getEmail());
            }
        }
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));
    }
}
