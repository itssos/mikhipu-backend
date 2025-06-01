package pe.getsemani.mikhipu.assistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = AssistanceSession.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AssistanceSession {

    // === Constantes ===
    public static final String TABLE_NAME = "assistance_sessions";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_ENTRY = "start_entry_time";
    public static final String COLUMN_END_ENTRY = "end_entry_time";
    public static final String COLUMN_START_EXIT = "start_exit_time";
    public static final String COLUMN_END_EXIT = "end_exit_time";
    public static final String COLUMN_DEADLINE = "attendance_deadline";
    public static final String COLUMN_CREATED_BY = "created_by";

    // === Atributos ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID)
    private Long id;

    @NotNull(message = "La fecha de sesión es obligatoria.")
    @Column(name = COLUMN_DATE, nullable = false)
    private LocalDate date;

    @NotNull(message = "La hora de inicio de entrada es obligatoria.")
    @Column(name = COLUMN_START_ENTRY, nullable = false)
    private LocalTime startEntryTime;

    @NotNull(message = "La hora de fin de entrada es obligatoria.")
    @Column(name = COLUMN_END_ENTRY, nullable = false)
    private LocalTime endEntryTime;

    @NotNull(message = "La hora de inicio de salida es obligatoria.")
    @Column(name = COLUMN_START_EXIT, nullable = false)
    private LocalTime startExitTime;

    @NotNull(message = "La hora de fin de salida es obligatoria.")
    @Column(name = COLUMN_END_EXIT, nullable = false)
    private LocalTime endExitTime;

    @NotNull(message = "La fecha límite de edición es obligatoria.")
    @Future(message = "La fecha límite debe ser en el futuro.")
    @Column(name = COLUMN_DEADLINE, nullable = false)
    private LocalDateTime attendanceDeadline;

}
