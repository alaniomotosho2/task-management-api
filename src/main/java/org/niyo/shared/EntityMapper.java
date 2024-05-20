package org.niyo.shared;


import org.mapstruct.factory.Mappers;
import org.niyo.task.Task;
import org.niyo.task.dto.TaskDto;
import org.niyo.task.dto.TaskResponse;
import org.niyo.user.User;
import org.niyo.user.UserResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,typeConversionPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class );

    UserResponse toUserResponse(User user);


    @Mapping(target = "assignee", ignore = true)
    Task toTask(TaskDto taskDto);

    @Mapping(source = "assignee", target = "assignee")
    TaskResponse toTaskResponse(Task task);

    List<TaskResponse> toTaskResponseList(List<Task> tasks);

}