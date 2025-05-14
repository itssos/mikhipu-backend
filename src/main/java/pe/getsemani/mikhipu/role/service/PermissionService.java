package pe.getsemani.mikhipu.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del permiso debe ser un número positivo.");
        }

        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
    }

    public Permission getPermissionByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso no puede estar vacío.");
        }

        return permissionRepository.findByName(name.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con nombre: " + name));
    }

    public Permission createPermission(Permission permission) {
        validatePermission(permission, true);

        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new IllegalStateException("Ya existe un permiso con el nombre: " + permission.getName());
        }

        return permissionRepository.save(permission);
    }

    public Permission updatePermission(Integer id, Permission updatedPermission) {
        validatePermission(updatedPermission, false);

        Permission existing = getPermissionById(id);

        if (!existing.getName().equalsIgnoreCase(updatedPermission.getName())) {
            throw new IllegalStateException("El nombre del permiso no puede ser modificado.");
        }

        existing.setLabel(updatedPermission.getLabel());

        return permissionRepository.save(existing);
    }

    public void deletePermission(Integer id) {
        Permission permission = getPermissionById(id);
        permissionRepository.delete(permission);
    }

    private void validatePermission(Permission permission, boolean isNew) {
        if (permission == null) {
            throw new IllegalArgumentException("El objeto permiso no puede ser nulo.");
        }

        if (isNew && permission.getId() != null) {
            throw new IllegalArgumentException("Un nuevo permiso no debe tener un ID asignado.");
        }

        if (permission.getName() == null || permission.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso es obligatorio.");
        }

        if (permission.getName().length() < 3 || permission.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre del permiso debe tener entre 3 y 50 caracteres.");
        }

        if (permission.getLabel() == null || permission.getLabel().trim().isEmpty()) {
            throw new IllegalArgumentException("La etiqueta del permiso es obligatoria.");
        }

        if (permission.getLabel().length() < 3 || permission.getLabel().length() > 50) {
            throw new IllegalArgumentException("La etiqueta debe tener entre 3 y 50 caracteres.");
        }
    }
}
