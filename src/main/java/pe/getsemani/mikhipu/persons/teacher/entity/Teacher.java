package pe.getsemani.mikhipu.persons.teacher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.course.entity.Course;
import pe.getsemani.mikhipu.persons.person.entity.Person;

import java.util.Set;

@Entity
@Table(name = Teacher.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Teacher {

    // === Constantes para columnas y configuración ===
    public static final String TABLE_NAME = "teachers";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PERSON_ID = "person_id";
    public static final String COLUMN_CODE = "code";

    public static final int CODE_MAX_LENGTH = 20;
    public static final int CODE_MIN_LENGTH = 4;

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = COLUMN_PERSON_ID, nullable = false, unique = true)
    private Person person;

    @NotBlank(message = "El código del docente no puede estar vacío.")
    @Size(min = CODE_MIN_LENGTH, max = CODE_MAX_LENGTH,
            message = "El código del docente debe tener entre " + CODE_MIN_LENGTH + " y " + CODE_MAX_LENGTH + " caracteres.")
    @Column(name = COLUMN_CODE, nullable = false, unique = true, length = CODE_MAX_LENGTH)
    private String code;

    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    private Set<Course> courses;
}