package uk.gov.hmcts.reform.dev.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.models.Task;

@Getter
@Setter
public class UpdateStatusDto {

    @NotNull(message = "Status must not be null")
    private Task.Status status;
}
