package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    // Mock TaskRepository class to stub its methods
    @Mock
    private TaskRepository taskRepository;

    // Instantiate userService and inject mocks
    @InjectMocks
    private TaskService taskService;

    @Nested
    @DisplayName("getAllTasks tests")
    class GetAllTasksTests {
        @Test
        void getAllTasks_shouldReturnAllTasks() {
            // Arrange
            Task task1 = new Task(UUID.randomUUID(), "Task 1", "Description 1", Task.Status.PENDING, LocalDateTime.now());
            Task task2 = new Task(UUID.randomUUID(), "Task 2", "Description 2", Task.Status.IN_PROGRESS, LocalDateTime.now());
            List<Task> mockTasks = Arrays.asList(task1, task2);

            when(taskRepository.findAll()).thenReturn(mockTasks);

            // Act
            List<Task> result = taskService.getAllTasks();

            // Assert
            assertThat(result).isEqualTo(mockTasks);
            verify(taskRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("createTask tests")
    class CreateTaskTests {
        @Test
        public void createTask_ValidTask_ReturnsSavedTask() {
            // Arrange
            Task inputTask = new Task();
            inputTask.setTitle("New Task");
            inputTask.setDescription("Task Description");

            Task savedTask = new Task();
            savedTask.setId(UUID.randomUUID());
            savedTask.setTitle("New Task");
            savedTask.setDescription("Task Description");

            when(taskRepository.save(inputTask)).thenReturn(savedTask);

            // Act
            Task result = taskService.createTask(inputTask);

            // Assert
            assertNotNull(result);
            assertEquals(savedTask.getId(), result.getId());
            assertEquals(savedTask.getTitle(), result.getTitle());
            assertEquals(savedTask.getDescription(), result.getDescription());
            verify(taskRepository, times(1)).save(inputTask);
        }

        @Test
        public void createTask_NullTask_ThrowsException() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> taskService.createTask(null));
            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getTaskById tests")
    class GetTaskByIdTests {
        @Test
        public void getTaskById_ExistingTask_ReturnsTask() {
            // Arrange
            UUID testId = UUID.randomUUID();
            Task expectedTask = new Task(testId, "Test Task", "Test Description", Task.Status.PENDING, LocalDateTime.now());
            when(taskRepository.findById(testId)).thenReturn(Optional.of(expectedTask));

            // Act
            String testIdString = testId.toString();
            Task result = taskService.getTaskById(testIdString);

            // Assert
            assertNotNull(result);
            assertEquals(expectedTask.getId(), result.getId());
            assertEquals(expectedTask.getTitle(), result.getTitle());
            verify(taskRepository, times(1)).findById(testId);
        }

        @Test
        public void getTaskById_NonExistingTask_ThrowsTaskNotFoundException() {
            // Arrange
            UUID taskId = UUID.randomUUID();


            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act & Assert
            String taskIdString = taskId.toString();
            TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                                                           () -> taskService.getTaskById(taskIdString));

            assertEquals(taskId, exception.getTaskId());
            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void getTaskById_InvalidUUID_ThrowsIllegalArgumentException() {
            // Arrange
            String invalidId = "123";

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                         () -> taskService.getTaskById(invalidId));

            // Verify repository was never called with invalid ID
            verify(taskRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("updateTaskStatus tests")
    class UpdateTaskStatusTests {
        @Test
        public void updateTaskStatus_ValidIdAndStatus_ReturnsUpdatedTask() {
            // Arrange
            UUID testId = UUID.randomUUID();
            Task existingTask = new Task(testId, "Test Task", "Test Description", Task.Status.PENDING, LocalDateTime.now());
            Task.Status newStatus = Task.Status.COMPLETED;
            when(taskRepository.findById(testId)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(existingTask)).thenReturn(existingTask);

            // Act
            String testIdString = testId.toString();
            Task result = taskService.updateTaskStatus(testIdString, newStatus);

            // Assert
            assertNotNull(result);
            assertEquals(newStatus, result.getStatus());
            verify(taskRepository, times(1)).findById(testId);
            verify(taskRepository, times(1)).save(existingTask);
        }

        @Test
        public void updateTaskStatus_NonExistingTask_ThrowsTaskNotFoundException() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            Task.Status newStatus = Task.Status.COMPLETED;

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act & Assert
            String taskIdString = taskId.toString();
            TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                                                           () -> taskService.updateTaskStatus(taskIdString, newStatus));

            assertEquals(taskId, exception.getTaskId());
            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void updateTaskStatus_InvalidUUID_ThrowsIllegalArgumentException() {
            // Arrange
            String invalidId = "123";
            Task.Status newStatus = Task.Status.COMPLETED;

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                         () -> taskService.updateTaskStatus(invalidId, newStatus));

            // Verify repository was never called with invalid ID
            verify(taskRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("deleteTask tests")
    class DeleteTaskTests {
        @Test
        public void deleteTask_ExistingTask_DeletesTask() {
            // Arrange
            UUID testId = UUID.randomUUID();
            Task existingTask = new Task(testId, "Test Task", "Test Description", Task.Status.PENDING, LocalDateTime.now());
            when(taskRepository.findById(testId)).thenReturn(Optional.of(existingTask));

            // Act
            String testIdString = testId.toString();
            taskService.deleteTask(testIdString);

            // Assert
            verify(taskRepository, times(1)).findById(testId);
            verify(taskRepository, times(1)).delete(existingTask);
        }

        @Test
        public void deleteTask_NonExistingTask_ThrowsTaskNotFoundException() {
            // Arrange
            UUID taskId = UUID.randomUUID();

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act & Assert
            String taskIdString = taskId.toString();
            TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                                                           () -> taskService.deleteTask(taskIdString));

            assertEquals(taskId, exception.getTaskId());
            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void deleteTask_InvalidUUID_ThrowsIllegalArgumentException() {
            // Arrange
            String invalidId = "123";

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                         () -> taskService.deleteTask(invalidId));

            // Verify repository was never called with invalid ID
            verify(taskRepository, never()).findById(any());
        }
    }
}
