package pe.getsemani.mikhipu.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = Permission.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = Permission.COLUMN_NAME)
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Permission {

    // === Constantes de tabla y columnas ===
    public static final String TABLE_NAME = "permissions";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LABEL = "label";

    public static final int NAME_MAX_LENGTH = 50;
    public static final int NAME_MIN_LENGTH = 3;

    public static final int LABEL_MAX_LENGTH = 50;
    public static final int LABEL_MIN_LENGTH = 3;

    // === Atributos ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Integer id;

    @NotBlank(message = "El nombre del permiso no debe estar vacío.")
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH,
            message = "El nombre del permiso debe tener entre " + NAME_MIN_LENGTH + " y " + NAME_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_NAME, nullable = false, unique = true, length = NAME_MAX_LENGTH)
    private String name;

    @NotBlank(message = "La etiqueta del permiso no debe estar vacía.")
    @Size(min = LABEL_MIN_LENGTH, max = LABEL_MAX_LENGTH,
            message = "La etiqueta del permiso debe tener entre " + LABEL_MIN_LENGTH + " y " + LABEL_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_LABEL, nullable = false, length = LABEL_MAX_LENGTH)
    private String label;
}