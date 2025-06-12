package pe.getsemani.mikhipu.scores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.getsemani.mikhipu.scores.dto.ScoreResponseDTO;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepresentativeScoreHistoryDTO {
    private Long studentId;
    private String studentFullName;
    private List<ScoreResponseDTO> scores;
}
