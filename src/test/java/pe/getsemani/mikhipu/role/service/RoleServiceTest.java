package pe.getsemani.mikhipu.role.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.getsemani.mikhipu.exception.ResourceNotFoundException;
import pe.getsemani.mikhipu.role.entity.Role;
import pe.getsemani.mikhipu.role.enums.RoleType;
import pe.getsemani.mikhipu.role.repository.RoleRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de roles")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role sampleRole() {
        return Role.builder()
                .id(1)
                .name(RoleType.ADMINISTRADOR)
                .description("Administrador")
                .build();
    }

    @Test
    @DisplayName("createRole() debe guardar y retornar el rol")
    void createRole_savesAndReturnsRole() {
        Role role = sampleRole();
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.createRole(role);

        assertThat(result).isSameAs(role);
        verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("getRoleById() debe retornar rol existente")
    void getRoleById_existingId_returnsRole() {
        Role role = sampleRole();
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(1);

        assertThat(result).isSameAs(role);
    }

    @Test
    @DisplayName("getRoleById() lanza excepción si no existe")
    void getRoleById_nonExistingId_throwsException() {
        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role with id 99 not found");
    }

    @Test
    @DisplayName("getAllRoles() debe retornar todos los roles")
    void getAllRoles_returnsListOfRoles() {
        Role r1 = sampleRole();
        Role r2 = Role.builder().id(2).name(RoleType.DOCENTE).description("Docente").build();
        when(roleRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Role> result = roleService.getAllRoles();

        assertThat(result).hasSize(2).containsExactly(r1, r2);
    }

    @Test
    @DisplayName("updateRole() debe actualizar y retornar el rol")
    void updateRole_existingId_updatesAndReturnsRole() {
        Role existing = sampleRole();
        Role details = Role.builder()
                .name(RoleType.ESTUDIANTE)
                .description("Estudiante")
                .build();
        when(roleRepository.findById(1)).thenReturn(Optional.of(existing));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role result = roleService.updateRole(1, details);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo(RoleType.ESTUDIANTE);
        assertThat(result.getDescription()).isEqualTo("Estudiante");
        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        Role saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(details.getName());
    }

    @Test
    @DisplayName("deleteRole() debe eliminar el rol existente")
    void deleteRole_existingId_deletesRole() {
        Role role = sampleRole();
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        roleService.deleteRole(1);

        verify(roleRepository).delete(role);
    }

    @Test
    @DisplayName("getRoleByType() debe retornar rol por tipo")
    void getRoleByType_existingType_returnsRole() {
        Role role = sampleRole();
        when(roleRepository.findByName(RoleType.ADMINISTRADOR)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleByType(RoleType.ADMINISTRADOR);

        assertThat(result).isSameAs(role);
    }

    @Test
    @DisplayName("getRoleByType() lanza excepción si no encuentra el tipo")
    void getRoleByType_nonExistingType_throwsException() {
        when(roleRepository.findByName(RoleType.ESTUDIANTE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleByType(RoleType.ESTUDIANTE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role ESTUDIANTE not found");
    }
}
