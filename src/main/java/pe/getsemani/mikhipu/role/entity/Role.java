package pe.getsemani.mikhipu.role.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = @UniqueConstraint(columnNames = Role.COLUMN_NAME)
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {

    // === Constantes de columna ===
    public static final String TABLE_NAME = "roles";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final int NAME_MAX_LENGTH = 50;
    public static final int NAME_MIN_LENGTH = 3;

    public static final int DESCRIPTION_MAX_LENGTH = 100;

    // === Atributos ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Integer id;

    @NotNull(message = "El nombre del rol no debe ser nulo.")
    @NotEmpty(message = "El nombre del rol no puede estar vacío.")
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH,
            message = "El nombre del rol debe tener entre " + NAME_MIN_LENGTH + " y " + NAME_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_NAME, length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;

    @Size(max = DESCRIPTION_MAX_LENGTH,
            message = "La descripción debe tener como máximo " + DESCRIPTION_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_DESCRIPTION, length = DESCRIPTION_MAX_LENGTH)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"})
    )
    private Set<Permission> permissions = new HashSet<>();
}
