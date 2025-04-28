package uk.gov.hmcts.reform.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.dto.request.CreateTaskRequestDto;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// This is technically a web integration test rather than a pure unit test,
// however I think this is preferable to a pure unit test because it tests the controller in a more realistic environment
@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock TaskService class to stub its methods
    @MockitoBean
    private TaskService taskService;

    // Instantiate userService and inject mocks
    @InjectMocks
    private TaskController taskController;

    // Mock ObjectMapper class to handle JSON serialization/deserialization
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("createTask tests")
    class CreateTaskTests {
        @Test
        void createTask_shouldReturn201_withCreatedTask() throws Exception {
            // Arrange - create a valid request DTO
            CreateTaskRequestDto testRequest = new CreateTaskRequestDto("Complete testing suite", "Complete the testing suite for you coding task", Task.Status.IN_PROGRESS, LocalDateTime.of(2025, 7, 2, 13, 30));

            // Mock the TaskService to return a Task object based on the dto when createTask is called
            Task savedTask = new Task(UUID.randomUUID(), "Complete testing suite", "Complete the testing suite for you coding task", Task.Status.IN_PROGRESS, LocalDateTime.of(2025, 7, 2, 13, 30));
            when(taskService.createTask(any())).thenReturn(savedTask);

            // Act & Assert - perform a POST request on the endpoint and check the response
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    jsonPath("$.title").value("Complete testing suite"),
                    jsonPath("$.description").value("Complete the testing suite for you coding task"),
                    jsonPath("$.status").value("IN_PROGRESS"),
                    jsonPath("$.dueDateTime").value("2025-07-02T13:30:00")
                );
        }

        @Test
        void createTask_shouldReturn400_whenRequestInvalid() throws Exception {
            // Arrange - create an invalid request DTO (empty in this case)
            CreateTaskRequestDto invalidRequest = new CreateTaskRequestDto();

            // Act & Assert - perform a POST request on the endpoint and check the response
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            // Verify that the service method was never called
            verify(taskService, never()).createTask(any());
        }
    }

    @Nested
    @DisplayName("getAllTasks tests")
    class GetAllTasksTests {
        @Test
        void getAllTasks_shouldReturnAllTasks() throws Exception {
            // Arrange - create a list of tasks to be returned by the mock service
            Task task1 = new Task(UUID.randomUUID(), "Task 1", "Description 1", Task.Status.PENDING, LocalDateTime.of(2025, 5, 1, 18, 0));
            Task task2 = new Task(UUID.randomUUID(), "Task 2", "Description 2", Task.Status.IN_PROGRESS, LocalDateTime.of(2025, 6, 1, 18, 0));

            List<Task> mockTasks = Arrays.asList(task1, task2);
            when(taskService.getAllTasks()).thenReturn(mockTasks);

            // Act & Assert - perform a GET request on the endpoint and check the response returns all tasks as JSON
            mockMvc.perform(get("/tasks")
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].dueDateTime").value("2025-05-01T18:00:00"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].dueDateTime").value("2025-06-01T18:00:00"));
            verify(taskService, times(1)).getAllTasks();
        }
    }

    @Nested
    @DisplayName("getTaskById tests")
    class GetTaskByIdTests {
        @Test
        void getTaskById_shouldReturnTask_whenValid() throws Exception {
            // Arrange - create a task to be returned by the mock service
            UUID testId = UUID.randomUUID();
            Task task = new Task(testId, "Task 1", "Description 1", Task.Status.PENDING, LocalDateTime.of(2025, 1, 1, 1, 0));
            when(taskService.getTaskById(testId.toString())).thenReturn(task);

            // Act & Assert - perform a GET request on the endpoint and check the response returns the task as JSON
            mockMvc.perform(get("/tasks/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.dueDateTime").value("2025-01-01T01:00:00"));
        }

        @Test
        void getTaskById_shouldReturn404_whenNotFound() throws Exception {
            // Arrange - create a UUID that does not exist in the mock service
            UUID testId = UUID.randomUUID();
            when(taskService.getTaskById(testId.toString())).thenThrow(new TaskNotFoundException(testId));

            // Act & Assert - perform a GET request on the endpoint and check the response returns 404
            mockMvc.perform(get("/tasks/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("updateTaskStatus tests")
    class UpdateTaskStatusTests {
        @Test
        void updateTaskStatus_shouldReturnUpdatedTask_whenUpdateSuccessful() throws Exception {
            // Arrange - create an initial task
            UUID testId = UUID.randomUUID();
            Task initialTask = new Task(testId, "Task 1", "Description 1", Task.Status.PENDING, LocalDateTime.of(2025, 1, 1, 1, 0));

            // Create a task with the expected updated status
            Task updatedTask = new Task(testId, "Task 1", "Description 1", Task.Status.IN_PROGRESS, LocalDateTime.of(2025, 1, 1, 1, 0));

            // Mock the service to return the updated task
            when(taskService.updateTaskStatus(testId.toString(), Task.Status.IN_PROGRESS)).thenReturn(updatedTask);

            // Act & Assert
            mockMvc.perform(patch("/tasks/{id}", testId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")) // Corrected assertion
                .andExpect(jsonPath("$.dueDateTime").value("2025-01-01T01:00:00"));
        }

        @Test
        void updateTaskStatus_shouldReturn404_whenNotFound() throws Exception {
            // Arrange - create a UUID that does not exist in the mock service
            UUID testId = UUID.randomUUID();
            when(taskService.updateTaskStatus(testId.toString(), Task.Status.IN_PROGRESS)).thenThrow(new TaskNotFoundException(testId));

            // Act & Assert - perform a PATCH request on the endpoint and check the response returns 404
            mockMvc.perform(post("/tasks/{id}/status", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("deleteTask tests")
    class DeleteTaskTests {
        @Test
        void deleteTask_shouldCallReturn204_whenSuccessful() throws Exception {
            // Arrange - create a random UUID to delete
            UUID testId = UUID.randomUUID();

            // Act & Assert - perform a DELETE request on the endpoint and check the response returns 204
            mockMvc.perform(delete("/tasks/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Verify that the service method deleteTask was called once
            verify(taskService, times(1)).deleteTask(testId.toString());
        }

        @Test
        void deleteTask_shouldReturn404_whenNotFound() throws Exception {
            // Arrange - create a UUID that does not exist in the mock service
            UUID testId = UUID.randomUUID();
            doThrow(new TaskNotFoundException(testId)).when(taskService).deleteTask(testId.toString());

            // Act & Assert - perform a DELETE request on the endpoint and check the response returns 404
            mockMvc.perform(delete("/tasks/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }
    }
}
