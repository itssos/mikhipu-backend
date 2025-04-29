package pe.getsemani.mikhipu.user.mapper;

import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;
import pe.getsemani.mikhipu.user.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setPermissions(
                user.getRole() != null && user.getRole().getPermissions() != null
                        ? user.getRole().getPermissions()
                        .stream()
                        .map(permission -> permission.getName())
                        .collect(Collectors.toSet())
                        : null
        );
        return dto;
    }

    public static User fromCreateDto(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        // Nota: aquí no asignamos Role directamente porque deberías cargar la entidad Role por nombre en el Service
        return user;
    }
}