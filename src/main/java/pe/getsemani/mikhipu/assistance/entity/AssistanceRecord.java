package pe.getsemani.mikhipu.assistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.assistance.enums.AssistanceEntryStatus;
import pe.getsemani.mikhipu.assistance.enums.AssistanceExitStatus;
import pe.getsemani.mikhipu.persons.student.entity.Student;

import java.time.LocalDateTime;

@Entity
@Table(name = AssistanceRecord.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AssistanceRecord {

    // === Constantes ===
    public static final String TABLE_NAME = "assistance_records";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SESSION_ID = "session_id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_ENTRY_TIME = "entry_marked_at";
    public static final String COLUMN_ENTRY_STATUS = "entry_status";
    public static final String COLUMN_EXIT_TIME = "exit_marked_at";
    public static final String COLUMN_EXIT_STATUS = "exit_status";
    public static final String COLUMN_EDITED = "edited";
    public static final String COLUMN_EDITOR_ID = "editor_id";

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotNull(message = "La sesión de asistencia es obligatoria.")
    @ManyToOne
    @JoinColumn(name = COLUMN_SESSION_ID, nullable = false)
    private AssistanceSession session;

    @NotNull(message = "El alumno es obligatorio.")
    @ManyToOne
    @JoinColumn(name = COLUMN_STUDENT_ID, nullable = false)
    private Student student;

    @Column(name = COLUMN_ENTRY_TIME)
    private LocalDateTime entryMarkedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ENTRY_STATUS)
    private AssistanceEntryStatus entryStatus;

    @Column(name = COLUMN_EXIT_TIME)
    private LocalDateTime exitMarkedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_EXIT_STATUS)
    private AssistanceExitStatus exitStatus;

    @Column(name = COLUMN_EDITED, nullable = false)
    private Boolean edited = false;

}
