package org.niyo.task.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    @NotBlank(message = "Task's title is required")
    @Length(min=3,message = "title length is too small")
    private String title;

    private String description;

    @NotNull(message = "you must set task priority")
    private TaskPriority priority;

    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    private Long assignee;

    @NotNull(message = "start date is required")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "end date is required")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completionDate;

    @Column
    private String tags;
}