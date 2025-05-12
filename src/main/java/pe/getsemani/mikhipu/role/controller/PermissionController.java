package pe.getsemani.mikhipu.role.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_PERMISSIONS')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_PERMISSION')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Integer id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('GET_PERMISSION')")
    public ResponseEntity<Permission> getPermissionByName(@PathVariable String name) {
        return ResponseEntity.ok(permissionService.getPermissionByName(name));
    }
}
