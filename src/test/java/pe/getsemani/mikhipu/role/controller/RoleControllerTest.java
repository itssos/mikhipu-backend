package pe.getsemani.mikhipu.role.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.service.RoleService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@WithMockUser(roles = "ADMINISTRADOR") // Simula autorización de Spring Security
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1)
                .name(RoleType.DOCENTE)
                .description("Rol de Docente")
                .build();
    }

    @Test
    @DisplayName("POST /api/roles - Crear un rol")
    void testCreateRole() throws Exception {
        Mockito.when(roleService.createRole(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/api/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName().toString()))
                .andExpect(jsonPath("$.description").value(role.getDescription()));
    }

    @Test
    @DisplayName("GET /api/roles/{id} - Obtener rol por ID")
    void testGetRoleById() throws Exception {
        Mockito.when(roleService.getRoleById(1)).thenReturn(role);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName().toString()))
                .andExpect(jsonPath("$.description").value(role.getDescription()));
    }

    @Test
    @DisplayName("GET /api/roles - Listar todos los roles")
    void testGetAllRoles() throws Exception {
        Mockito.when(roleService.getAllRoles()).thenReturn(Arrays.asList(role));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(role.getId()))
                .andExpect(jsonPath("$[0].name").value(role.getName().toString()))
                .andExpect(jsonPath("$[0].description").value(role.getDescription()));
    }

    @Test
    @DisplayName("PUT /api/roles/{id} - Actualizar rol")
    void testUpdateRole() throws Exception {
        Role updated = Role.builder()
                .id(1)
                .name(RoleType.ESTUDIANTE)
                .description("Actualizado")
                .build();

        Mockito.when(roleService.updateRole(eq(1), any(Role.class))).thenReturn(updated);

        mockMvc.perform(put("/api/roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.description").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} - Eliminar rol")
    void testDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Nested
    @DisplayName("Validaciones y errores")
    class ErrorCases {

        @Test
        @DisplayName("POST /api/roles - Falla por nombre nulo")
        void testCreateRoleValidationFail() throws Exception {
            Role invalidRole = Role.builder()
                    .description("Sin nombre")
                    .build();

            mockMvc.perform(post("/api/roles")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRole)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/roles/{id} - Rol no encontrado")
        void testRoleNotFound() throws Exception {
            Mockito.when(roleService.getRoleById(99))
                    .thenThrow(new pe.getsemani.mikhipu.exception.ResourceNotFoundException("Not found"));

            mockMvc.perform(get("/api/roles/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
