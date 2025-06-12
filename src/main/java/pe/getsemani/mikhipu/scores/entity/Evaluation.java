package pe.getsemani.mikhipu.scores.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import pe.getsemani.mikhipu.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.scores.enums.EvaluationType;

@Entity
@Table(name = Evaluation.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Evaluation {

    // === Constantes ===
    public static final String TABLE_NAME = "evaluations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MIN_SCORE = "min_score";
    public static final String COLUMN_MAX_SCORE = "max_score";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotNull(message = "El curso es obligatorio.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_COURSE_ID, nullable = false)
    private Course course;

    @NotBlank(message = "El nombre de la evaluación es obligatorio.")
    @Size(max = 100)
    @Column(name = COLUMN_NAME, nullable = false, length = 100)
    private String name;

    @NotNull(message = "El tipo de evaluación es obligatorio.")
    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_TYPE, nullable = false, length = 20)
    private EvaluationType type;

    @NotNull(message = "La ponderación es obligatoria.")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    @Column(name = COLUMN_WEIGHT, nullable = false)
    private Double weight; // porcentaje para el promedio

    @NotNull(message = "La fecha de la evaluación es obligatoria.")
    @Column(name = COLUMN_DATE, nullable = false)
    private LocalDate date;

    @NotNull(message = "El puntaje mínimo es obligatorio.")
    @Column(name = COLUMN_MIN_SCORE, nullable = false)
    private Double minScore;

    @NotNull(message = "El puntaje máximo es obligatorio.")
    @Column(name = COLUMN_MAX_SCORE, nullable = false)
    private Double maxScore;

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