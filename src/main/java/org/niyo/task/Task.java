package org.niyo.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;
import org.niyo.user.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private User modifiedBy;

    @Column(name = "creation_date",nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date",nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(name = "start_date",nullable = false)
    private LocalDateTime startDate;

    @Column(name = "completion_date",nullable = false)
    private LocalDateTime completionDate;

    @Column
    private String tags;
}