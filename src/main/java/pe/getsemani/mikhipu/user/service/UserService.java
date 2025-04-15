package pe.getsemani.mikhipu.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User assignRole(Integer userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario de ID: " + userId));

        if (user.getRoles() != null && user.getRoles().stream().anyMatch(r -> "ADMINISTRADOR".equals(r.getName()))) {
            throw new IllegalStateException("El rol ADMINISTRADOR no se puede modificar");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el rol: " + roleName));

        if(user.getRoles() == null){
            user.setRoles(new HashSet<>());
        }
        user.getRoles().clear();
        user.getRoles().add(role);

        return userRepository.save(user);
    }
}
