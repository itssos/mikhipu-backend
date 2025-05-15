package pe.getsemani.mikhipu.person.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
import pe.getsemani.mikhipu.person.enums.RelationshipType;
import pe.getsemani.mikhipu.persons.person.entity.Person;

import java.util.Set;

@Entity
@Table(name = "representatives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Representative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

    @NotNull(message = "El parentesco es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", length = 30)
    private RelationshipType relationship;

    @JsonIgnore
    @ManyToMany(mappedBy = "representatives")
    private Set<Student> students;
}