package uk.gov.hmcts.reform.dev.repositories;

import uk.gov.hmcts.reform.dev.models.Task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Automatically creates basic CRUD operations for Task entity - no need to define them explicitly
}
