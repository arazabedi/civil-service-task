import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description; // Optional field

    @NotNull(message = "Status is required")
    private Task.Status status;

    @NotNull(message = "Due date and time are required")
    private LocalDateTime dueDateTime;
}
