package pe.getsemani.mikhipu.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        validateRole(role, true);

        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalStateException("Ya existe un rol con el nombre: " + role.getName());
        }

        return roleRepository.save(role);
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + name));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll().stream()
                .filter(role -> !"ADMINISTRADOR".equalsIgnoreCase(role.getName()))
                .collect(Collectors.toList());
    }

    public Role updateRole(Integer id, Role roleDetails) {
        validateRole(roleDetails, false);

        Role existingRole = getRoleById(id);

        if (!existingRole.getName().equalsIgnoreCase(roleDetails.getName())) {
            throw new IllegalStateException("El nombre del rol no se puede modificar.");
        }

        existingRole.setDescription(roleDetails.getDescription());
        existingRole.setPermissions(roleDetails.getPermissions());

        return roleRepository.save(existingRole);
    }

    public void deleteRole(Integer id) {
        Role role = getRoleById(id);

        if (isProtectedRole(role.getName())) {
            throw new IllegalStateException("No se puede eliminar el rol protegido: " + role.getName());
        }

        roleRepository.delete(role);
    }

    private void validateRole(Role role, boolean isNew) {
        if (role == null) {
            throw new IllegalArgumentException("El objeto rol no puede ser nulo.");
        }

        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }

        if (role.getName().length() < 3 || role.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre del rol debe tener entre 3 y 50 caracteres.");
        }

        if (role.getDescription() != null && role.getDescription().length() > 100) {
            throw new IllegalArgumentException("La descripción del rol no debe superar los 100 caracteres.");
        }

        if (isNew && role.getId() != null) {
            throw new IllegalArgumentException("El nuevo rol no debe tener un ID asignado.");
        }
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            List<Integer> permissionIds = role.getPermissions()
                    .stream()
                    .map(Permission::getId)
                    .toList();

            List<Integer> existingIds = permissionRepository.findAllById(permissionIds)
                    .stream()
                    .map(Permission::getId)
                    .toList();

            List<Integer> invalidIds = permissionIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .toList();

            if (!invalidIds.isEmpty()) {
                throw new IllegalArgumentException("Los siguientes IDs de permisos no existen: " + invalidIds);
            }
        }
    }

    private boolean isProtectedRole(String roleName) {
        return List.of("ADMINISTRADOR", "ESTUDIANTE", "DOCENTE", "APODERADO")
                .stream()
                .anyMatch(protectedName -> protectedName.equalsIgnoreCase(roleName));
    }
}