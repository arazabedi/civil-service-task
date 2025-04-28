package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.reform.dev.dto.request.CreateTaskRequestDto;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create a new task
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequestDto requestDto, BindingResult result) {
        // Checks if the request body is valid based on the DTO's requirements
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }

        // Create a Task object using the data in the request DTO
        Task newTask = mapToTask(requestDto);

        // Save the task to the database through the service method
        Task createdTask = taskService.createTask(newTask);

        // Return the created task with a 201 Created status
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Helper method to map the data from CreateTaskRequestDto into a Task object
    private Task mapToTask(CreateTaskRequestDto requestDto) {
        return Task.builder()
            .title(requestDto.getTitle())
            .description(requestDto.getDescription()) // Will still work if description is null
            .status(requestDto.getStatus())
            .dueDateTime(requestDto.getDueDateTime())
            .build();
    }

    // Get all tasks
    @GetMapping()
    public List<Task> getAllTasks() {
        List<Task> allTasks = taskService.getAllTasks();
        return ok(allTasks).getBody();
    }

    // Get a task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id); // throws TaskNotFoundException if not found
        return ResponseEntity.ok(task);
    }

    // Update the status of a task
    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody uk.gov.hmcts.reform.dev.dto.request.UpdateStatusRequestDto dto) {
        Task updatedTask = taskService.updateTaskStatus(id, dto.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
