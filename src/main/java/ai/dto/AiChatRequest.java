package ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    @NotBlank
    private String message;
    private LocalDate from;
    private LocalDate to;
    private LocalDate compareFrom;
    private LocalDate compareTo;
    @Min(1)
    @Max(60)
    private Integer horizonDays;
    private Integer categoryId;
}
