package pe.getsemani.mikhipu.user.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.service.RoleService;
import pe.getsemani.mikhipu.user.dto.UserCreateDTO;
import pe.getsemani.mikhipu.user.dto.UserResponseDTO;
import pe.getsemani.mikhipu.user.entity.User;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleService roleService;

    @Autowired
    public UserMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public UserResponseDTO toDto(User user) {
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
                        .map(Permission::getName)
                        .collect(Collectors.toSet())
                        : null
        );
        return dto;
    }

    public User fromCreateDto(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        if (dto.getRole() != null) {
            Role role = roleService.getRoleByName(dto.getRole());
            user.setRole(role);
        }
        return user;
    }
}
