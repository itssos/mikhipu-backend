package pe.getsemani.mikhipu.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.dto.RoleAssignmentDTO;
import pe.getsemani.mikhipu.user.service.UserService;
import jakarta.validation.Valid;

@PreAuthorize("hasRole('ADMINISTRADOR')")
@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final UserService userService;

    @Autowired
    public UserRoleController(UserService userService){
        this.userService = userService;
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<User> assignRoleToUser(@PathVariable Integer userId,
                                                 @Valid @RequestBody RoleAssignmentDTO roleAssignmentDTO) {
        User updatedUser = userService.assignRole(userId, roleAssignmentDTO.getRoleType());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}