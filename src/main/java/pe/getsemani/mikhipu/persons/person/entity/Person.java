package pe.getsemani.mikhipu.persons.person.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.person.enums.Gender;
import pe.getsemani.mikhipu.user.entity.User;
import java.time.LocalDate;

@Entity
@Table(name = Person.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Person {

    // === Constantes ===
    public static final String TABLE_NAME = "persons";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_DNI = "dni";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_USER_ID = "user_id";

    public static final int NAME_MAX_LENGTH = 100;
    public static final int DNI_LENGTH = 8;
    public static final int ADDRESS_MAX_LENGTH = 150;
    public static final int PHONE_MAX_LENGTH = 15;

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = NAME_MAX_LENGTH, message = "El nombre debe tener como máximo " + NAME_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_FIRST_NAME, nullable = false, length = NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = NAME_MAX_LENGTH, message = "El apellido debe tener como máximo " + NAME_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_LAST_NAME, nullable = false, length = NAME_MAX_LENGTH)
    private String lastName;

    @NotBlank(message = "El DNI no puede estar vacío.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos.")
    @Column(name = COLUMN_DNI, nullable = false, unique = true, length = DNI_LENGTH)
    private String dni;

    @NotNull(message = "El género es obligatorio.")
    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_GENDER, nullable = false)
    private Gender gender;

    @NotBlank(message = "La dirección no puede estar vacía.")
    @Size(max = ADDRESS_MAX_LENGTH, message = "La dirección debe tener como máximo " + ADDRESS_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_ADDRESS, nullable = false, length = ADDRESS_MAX_LENGTH)
    private String address;

    @NotBlank(message = "El número de teléfono no puede estar vacío.")
    @Size(max = PHONE_MAX_LENGTH, message = "El número de teléfono debe tener como máximo " + PHONE_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_PHONE, nullable = false, length = PHONE_MAX_LENGTH)
    private String phone;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    @Column(name = COLUMN_BIRTH_DATE, nullable = false)
    private LocalDate birthDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = COLUMN_USER_ID, nullable = false, unique = true)
    private User user;
}