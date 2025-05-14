package pe.getsemani.mikhipu.password.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = PasswordResetToken.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PasswordResetToken {

    // === Constantes ===
    public static final String TABLE_NAME = "password_reset_tokens";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final int TOKEN_MAX_LENGTH = 100;

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotBlank(message = "El token de restablecimiento no debe estar vacío.")
    @Column(name = COLUMN_TOKEN, nullable = false, unique = true, length = TOKEN_MAX_LENGTH)
    private String token;

    @NotNull(message = "El usuario asociado al token no puede ser nulo.")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_USER_ID, nullable = false)
    private User user;

    @NotNull(message = "La fecha de expiración del token no puede ser nula.")
    @Column(name = COLUMN_EXPIRY_DATE, nullable = false)
    private LocalDateTime expiryDate;
}