package pe.getsemani.mikhipu.person.entity;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.person.enums.RelationshipType;

import java.util.Set;

@Entity
@DiscriminatorValue("APODERADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Representative extends Person {

    @NotNull(message = "El parentesco es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", length = 30)
    private RelationshipType relationship;

    @ManyToMany(mappedBy = "representatives")
    private Set<Student> students;
}