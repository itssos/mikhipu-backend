package pe.getsemani.mikhipu.person.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.DayOfWeek;

@Data
public class TeacherScheduleDTO {
    @NotNull
    private Long teacherId;

    @NotNull
    private Long courseId;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Formato debe ser HH:mm")
    private String startTime; // luego se convierte a LocalTime
}