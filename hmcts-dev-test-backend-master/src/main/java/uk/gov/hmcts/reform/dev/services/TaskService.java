package uk.gov.hmcts.reform.dev.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TaskService {
    // Use final fields meaning they must be provided at object creation - Spring handles the rest
    private final TaskRepository taskRepository;

    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Retrieve a task by ID
    public Task getTaskById(String id) {
        // Convert the string ID to a UUID
        UUID parsedId = UUID.fromString(id);
        return taskRepository.findById(parsedId)
            .orElseThrow(() -> new TaskNotFoundException(parsedId));
    }

    // Retrieve all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Update the status of a task
    public Task updateTaskStatus(String id, Task.Status status) {
        Task task = getTaskById(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    // Delete a task
    public void deleteTask(String id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
