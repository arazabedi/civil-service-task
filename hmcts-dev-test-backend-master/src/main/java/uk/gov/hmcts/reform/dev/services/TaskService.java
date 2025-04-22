import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TaskService {
    // Use final fields meaning they must be provided at  object creation - Spring handles the rest
    private final TaskRepository taskRepository;

    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Retrieve a task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    // Retrieve all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Update the status of a task
    public Task updateTaskStatus(Long id, Task.Status status) {
        Task task = getTaskById(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    // Delete a task
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
