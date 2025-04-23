package uk.gov.hmcts.reform.dev.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

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
