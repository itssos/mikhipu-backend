package pe.getsemani.mikhipu.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.mapper.UserMapper;
import pe.getsemani.mikhipu.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserCreateDTO dto) {

        User user = UserMapper.fromCreateDto(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + dto.getRole()));
        user.setRole(role);
        User savedUser = userRepository.save(user);

        return UserMapper.toDto(savedUser);
    }

    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserMapper.toDto(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUser(Integer id, UserCreateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + dto.getRole()));
        user.setRole(role);

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }
}