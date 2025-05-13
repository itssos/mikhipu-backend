package pe.getsemani.mikhipu.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.role.service.RoleService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("DOCENTE");
        role.setDescription("Docente rol");
    }

    @Test
    @DisplayName("Debe crear un nuevo rol correctamente")
    void createRole_success() {
        when(roleRepository.save(role)).thenReturn(role);

        Role saved = roleService.createRole(role);

        assertNotNull(saved);
        assertEquals("DOCENTE", saved.getName());
    }

    @Test
    @DisplayName("Debe obtener rol por ID si existe")
    void getRoleById_success() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role found = roleService.getRoleById(1);

        assertEquals("DOCENTE", found.getName());
    }

    @Test
    @DisplayName("Debe lanzar excepción si rol no existe por ID")
    void getRoleById_notFound() {
        when(roleRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(2));
    }

    @Test
    @DisplayName("Debe obtener rol por nombre si existe")
    void getRoleByName_success() {
        when(roleRepository.findByName("DOCENTE")).thenReturn(Optional.of(role));

        Role found = roleService.getRoleByName("DOCENTE");

        assertEquals("DOCENTE", found.getName());
    }

    @Test
    @DisplayName("Debe lanzar excepción si rol no existe por nombre")
    void getRoleByName_notFound() {
        when(roleRepository.findByName("FAKE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByName("FAKE"));
    }

    @Test
    @DisplayName("Debe retornar todos los roles excepto ADMINISTRADOR")
    void getAllRoles_excludeAdmin() {
        Role admin = new Role();
        admin.setName("ADMINISTRADOR");
        Role docente = new Role();
        docente.setName("DOCENTE");

        when(roleRepository.findAll()).thenReturn(List.of(admin, docente));

        List<Role> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        assertEquals("DOCENTE", result.get(0).getName());
    }

    @Test
    @DisplayName("Debe actualizar la descripción del rol si el nombre coincide")
    void updateRole_success() {
        Role updateData = new Role();
        updateData.setName("DOCENTE");
        updateData.setDescription("Nuevo desc");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        Role updated = roleService.updateRole(1, updateData);

        assertEquals("Nuevo desc", updated.getDescription());
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta cambiar el nombre del rol")
    void updateRole_changeName_invalid() {
        Role updateData = new Role();
        updateData.setName("ALGO");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        assertThrows(IllegalStateException.class, () -> roleService.updateRole(1, updateData));
    }

    @Test
    @DisplayName("Debe eliminar rol si no es protegido")
    void deleteRole_success() {
        Role custom = new Role();
        custom.setName("OTRO");

        when(roleRepository.findById(1)).thenReturn(Optional.of(custom));

        roleService.deleteRole(1);

        verify(roleRepository).delete(custom);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar rol protegido")
    void deleteRole_protected() {
        Role protectedRole = new Role();
        protectedRole.setName("ESTUDIANTE");

        when(roleRepository.findById(1)).thenReturn(Optional.of(protectedRole));

        assertThrows(IllegalStateException.class, () -> roleService.deleteRole(1));
    }
}
