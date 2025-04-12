package pe.getsemani.mikhipu.person.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.person.enums.SchoolLevel;
import pe.getsemani.mikhipu.person.enums.Section;

import java.util.Set;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends Person {

    @NotNull(message = "El grado es obligatorio")
    @Min(value = 1, message = "El grado mínimo es 1")
    @Max(value = 6, message = "El grado máximo es 6")
    @Column(name = "grade", nullable = false)
    private Integer grade;

    @NotNull(message = "La sección es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false, length = 5)
    private Section section;

    @NotNull(message = "El nivel de la escuela es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "school_level", nullable = false, length = 10)
    private SchoolLevel schoolLevel;

    @ManyToMany
    @JoinTable(name = "student_representative",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "representative_id"))
    private Set<Representative> representatives;
}
