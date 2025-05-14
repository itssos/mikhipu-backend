package pe.getsemani.mikhipu.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.role.entity.Role;

import java.time.LocalDateTime;

@Entity
@Table(
        name = User.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = User.COLUMN_USERNAME),
                @UniqueConstraint(name = "uk_users_email", columnNames = User.COLUMN_EMAIL)
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    // === Constantes para columnas y longitudes ===
    public static final String TABLE_NAME = "users";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_ROLE_ID = "role_id";

    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 255;

    // === Atributos ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Integer id;

    @NotBlank(message = "El nombre de usuario no debe estar vacío.")
    @Size(max = USERNAME_MAX_LENGTH, message = "El nombre de usuario debe tener como máximo " + USERNAME_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_USERNAME, length = USERNAME_MAX_LENGTH, nullable = false)
    private String username;

    @Email(message = "El correo electrónico debe tener un formato válido.")
    @Size(max = EMAIL_MAX_LENGTH, message = "El correo electrónico debe tener como máximo " + EMAIL_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_EMAIL, length = EMAIL_MAX_LENGTH, unique = true)
    private String email;

    @NotBlank(message = "La contraseña no debe estar vacía.")
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = "La contraseña debe tener entre " + PASSWORD_MIN_LENGTH + " y " + PASSWORD_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_PASSWORD, length = PASSWORD_MAX_LENGTH, nullable = false)
    private String password;

    @Builder.Default
    @Column(name = COLUMN_ACTIVE, nullable = false)
    private boolean active = true;

    @Column(name = COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_ROLE_ID, nullable = false)
    private Role role;

    // === Auditoría automática ===
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}