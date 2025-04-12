package pe.getsemani.mikhipu.person.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private int successCount;
    private int failureCount;
    private List<String> errors;

}