package pe.getsemani.mikhipu.scores.entity;

import java.time.LocalDateTime;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.persons.student.entity.Student;

@Entity
@Table(name = Score.TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(columnNames = {Score.COLUMN_STUDENT_ID, Score.COLUMN_EVALUATION_ID})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Score {

    // === Constantes ===
    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_EVALUATION_ID = "evaluation_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotNull(message = "El estudiante es obligatorio.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_STUDENT_ID, nullable = false)
    private Student student;

    @NotNull(message = "La evaluación es obligatoria.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_EVALUATION_ID, nullable = false)
    private Evaluation evaluation;

    @NotNull(message = "El valor de la nota es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "La nota no puede ser menor al mínimo.")
    @Column(name = COLUMN_VALUE, nullable = false)
    private Double value;

    @Column(name = COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = COLUMN_UPDATED_AT)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}