package pe.getsemani.mikhipu.role.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.service.RolePermissionService;

@RestController
@RequestMapping("/api/roles/{roleId}/permissions")
public class RolePermissionController {

    private final RolePermissionService svc;
    public RolePermissionController(RolePermissionService svc) {
        this.svc = svc;
    }

    @PostMapping("/{permissionName}")
    @PreAuthorize("hasAuthority('ASSIGN_ROLE_PERMISSION')")
    public ResponseEntity<Role> assignPermissionToRole(
            @PathVariable Integer roleId,
            @PathVariable String permissionName) {
        return ResponseEntity.ok(svc.addPermissionToRole(roleId, permissionName));
    }

    @DeleteMapping("/{permissionName}")
    @PreAuthorize("hasAuthority('REMOVE_ROLE_PERMISSION')")
    public ResponseEntity<Role> removePermissionFromRole(
            @PathVariable Integer roleId,
            @PathVariable String permissionName) {
        return ResponseEntity.ok(svc.removePermissionFromRole(roleId, permissionName));
    }
}
