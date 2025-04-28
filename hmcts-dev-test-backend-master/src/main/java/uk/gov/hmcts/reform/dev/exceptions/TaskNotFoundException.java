package uk.gov.hmcts.reform.dev.exceptions;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID id) {
        super("Task not found with ID: " + id);
    }

    public UUID getTaskId() {
        return UUID.fromString(getMessage().split(": ")[1]);
    }
}
