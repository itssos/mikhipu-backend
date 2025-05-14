package pe.getsemani.mikhipu.role.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operaciones relacionadas con la gestión de roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Crear un nuevo rol", description = "Crea un nuevo rol con sus respectivos permisos asignados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o violación de reglas de negocio",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public ResponseEntity<Role> createRole(
            @Parameter(description = "Objeto Rol a crear", required = true)
            @Valid @RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(role));
    }

    @Operation(summary = "Obtener rol por ID", description = "Recupera la información de un rol específico usando su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_ROLE')")
    public ResponseEntity<Role> getRoleById(
            @Parameter(description = "ID del rol a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @Operation(summary = "Listar todos los roles", description = "Obtiene una lista de todos los roles disponibles, excluyendo el rol ADMINISTRADOR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class)))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('GET_ROLES')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "Actualizar un rol existente", description = "Actualiza la descripción y permisos de un rol existente. No se permite cambiar el nombre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o intento de modificar el nombre del rol", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public ResponseEntity<Role> updateRole(
            @Parameter(description = "ID del rol a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Detalles actualizados del rol", required = true)
            @Valid @RequestBody Role roleDetails) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDetails));
    }

    @Operation(summary = "Eliminar un rol", description = "Elimina un rol existente por su ID. No se permite eliminar roles protegidos como ADMINISTRADOR, ESTUDIANTE, DOCENTE o APODERADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Intento de eliminar un rol protegido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID del rol a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
