package pe.getsemani.mikhipu.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;

    @Transactional
    public Role addPermissionToRole(Integer roleId, String permName) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el rol de ID: " + roleId));
        Permission p = permRepo.findByName(permName)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el permiso: " + permName));
        role.getPermissions().add(p);
        return roleRepo.save(role);
    }

    @Transactional
    public Role removePermissionFromRole(Integer roleId, String permName) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el rol de ID: " + roleId));
        Permission p = permRepo.findByName(permName)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el permiso: " + permName));
        role.getPermissions().remove(p);
        return roleRepo.save(role);
    }
}