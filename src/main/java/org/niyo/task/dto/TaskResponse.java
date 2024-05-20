package org.niyo.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;
import org.niyo.user.UserResponse;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private UserResponse assignee;
    private UserResponse creator;
    private UserResponse modifiedBy;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime startDate;
    private LocalDateTime completionDate;
    private String tags;
}
