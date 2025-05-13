package pe.getsemani.mikhipu.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Permission;
import pe.getsemani.mikhipu.role.repository.PermissionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Tests de getAllPermissions")
    class GetAllPermissionsTest {
        @Test
        @DisplayName("Debe retornar todos los permisos")
        void debeRetornarTodosLosPermisos() {
            Permission permiso1 = new Permission();
            permiso1.setId(1);
            permiso1.setName("LEER");

            Permission permiso2 = new Permission();
            permiso2.setId(2);
            permiso2.setName("ESCRIBIR");

            List<Permission> permisosMock = Arrays.asList(permiso1, permiso2);

            when(permissionRepository.findAll()).thenReturn(permisosMock);

            List<Permission> resultado = permissionService.getAllPermissions();

            assertEquals(2, resultado.size());
            assertEquals("LEER", resultado.get(0).getName());
            verify(permissionRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Tests de getPermissionById")
    class GetPermissionByIdTest {
        @Test
        @DisplayName("Debe retornar un permiso por ID existente")
        void debeRetornarPermisoPorIdExistente() {
            Permission permiso = new Permission();
            permiso.setId(1);
            permiso.setName("LEER");

            when(permissionRepository.findById(1)).thenReturn(Optional.of(permiso));

            Permission resultado = permissionService.getPermissionById(1);

            assertNotNull(resultado);
            assertEquals("LEER", resultado.getName());
            verify(permissionRepository).findById(1);
        }

        @Test
        @DisplayName("Debe lanzar excepción si no existe el ID")
        void debeLanzarExcepcionSiIdNoExiste() {
            when(permissionRepository.findById(99)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                    permissionService.getPermissionById(99));

            assertTrue(ex.getMessage().contains("Permiso no encontrado con ID"));
            verify(permissionRepository).findById(99);
        }
    }

    @Nested
    @DisplayName("Tests de getPermissionByName")
    class GetPermissionByNameTest {
        @Test
        @DisplayName("Debe retornar un permiso por nombre existente")
        void debeRetornarPermisoPorNombreExistente() {
            Permission permiso = new Permission();
            permiso.setId(1);
            permiso.setName("ADMIN");

            when(permissionRepository.findByName("ADMIN")).thenReturn(Optional.of(permiso));

            Permission resultado = permissionService.getPermissionByName("ADMIN");

            assertNotNull(resultado);
            assertEquals(1, resultado.getId());
            verify(permissionRepository).findByName("ADMIN");
        }

        @Test
        @DisplayName("Debe lanzar excepción si no existe el nombre")
        void debeLanzarExcepcionSiNombreNoExiste() {
            when(permissionRepository.findByName("NO_EXISTE")).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                    permissionService.getPermissionByName("NO_EXISTE"));

            assertTrue(ex.getMessage().contains("Permiso no encontrado con nombre"));
            verify(permissionRepository).findByName("NO_EXISTE");
        }
    }
}
