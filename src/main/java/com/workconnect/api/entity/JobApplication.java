package com.workconnect.api.entity;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Enumerated(EnumType.STRING)
    private JobApplicationStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime statusUpdatedAt;
    private String coverLetter;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        statusUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        statusUpdatedAt = LocalDateTime.now();
    }
}
