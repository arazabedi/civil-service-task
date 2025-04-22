package uk.gov.hmcts.reform.dev.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.Entity;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caseNumber;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime dueDateTime;

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}
