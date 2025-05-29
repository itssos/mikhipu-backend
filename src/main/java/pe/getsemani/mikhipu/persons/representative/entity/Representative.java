package pe.getsemani.mikhipu.persons.representative.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pe.getsemani.mikhipu.persons.student.entity.Student;
import pe.getsemani.mikhipu.persons.representative.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.entity.Person;

import java.util.Set;

@Entity
@Table(name = Representative.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "students")
public class Representative {

    public static final String TABLE_NAME = "representatives";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PERSON_ID = "person_id";
    public static final String COLUMN_RELATIONSHIP = "relationship";
    public static final int RELATIONSHIP_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "La persona es obligatoria")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = COLUMN_PERSON_ID, nullable = false, unique = true)
    private Person person;

    @NotNull(message = "El parentesco es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_RELATIONSHIP, length = RELATIONSHIP_LENGTH, nullable = false)
    private RelationshipType relationship;

    @JsonIgnore
    @ManyToMany(mappedBy = "representatives")
    private Set<Student> students;
}