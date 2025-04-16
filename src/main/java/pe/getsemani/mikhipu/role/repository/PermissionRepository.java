package pe.getsemani.mikhipu.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.role.entity.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(String name);
}
