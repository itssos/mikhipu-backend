package pe.getsemani.mikhipu.role.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + id));
    }

    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .filter(role -> !"ADMINISTRADOR".equalsIgnoreCase(role.getName()))
                .collect(Collectors.toList());
    }

    public Role updateRole(Integer id, Role roleDetails) {
        Role role = getRoleById(id);
        if (!role.getName().equalsIgnoreCase(roleDetails.getName())) {
            throw new IllegalStateException("El nombre del rol no se puede modificar");
        }
        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }

    public void deleteRole(Integer id) {
        Role role = getRoleById(id);
        if (isProtectedRole(role.getName())) {
            throw new IllegalStateException("No se puede eliminar el rol " + role.getName());
        }
        roleRepository.delete(role);
    }

    private boolean isProtectedRole(String roleName) {
        return "ADMINISTRADOR".equalsIgnoreCase(roleName)
                || "ESTUDIANTE".equalsIgnoreCase(roleName)
                || "DOCENTE".equalsIgnoreCase(roleName)
                || "APODERADO".equalsIgnoreCase(roleName);
    }
}
