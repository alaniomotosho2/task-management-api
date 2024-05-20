package org.niyo.task;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.niyo.auth.AuthService;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;
import org.niyo.shared.EntityMapper;
import org.niyo.shared.PaginatedResponse;
import org.niyo.task.dto.TaskDto;
import org.niyo.task.dto.TaskResponse;
import org.niyo.user.User;
import org.niyo.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
@Blocking
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    @ConsumeEvent("task-stream")
    @Transactional(Transactional.TxType.NEVER)
    public List<TaskResponse> streamTasks(Void unused) { // Add a parameter, even if it's not used

        PanacheQuery<Task> resultSet = taskRepository.findAll();

        List<Task> tasks =  resultSet.stream().toList();
        return  EntityMapper.INSTANCE.toTaskResponseList(tasks);

    }

    public PaginatedResponse<TaskResponse> getAllTasks(int offset,int size) {

        PanacheQuery<Task> resultSet = taskRepository.findAll().page(Page.of(offset, size));

        List<Task> tasks =  resultSet.list();
        List<TaskResponse> taskResponses = EntityMapper.INSTANCE.toTaskResponseList(tasks);

        return new PaginatedResponse<>(taskResponses, offset, size,
            resultSet.list().size(), resultSet.count());
    }


    public TaskResponse getTaskById(Long id) {

        Task task =  taskRepository.findById(id);
        if(task == null){
            log.info("task not found");
            throw new WebApplicationException(String.format("Task with %d not found",id),404);
        }

        return EntityMapper.INSTANCE.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskDto taskDto) {
        validateConstant(taskDto);

        User assignee = taskDto.getAssignee() != null ? userService.findById(taskDto.getAssignee()) : null;
        if(assignee == null){
            throw new WebApplicationException("Assignee not found",404);
        }

        try{
            Task task = EntityMapper.INSTANCE.toTask(taskDto);
            task.setCreationDate(LocalDateTime.now());
            task.setCreator(authService.getCurrentUser());
            task.setAssignee(assignee);
            taskRepository.persist(task);

            if(task.getStartDate().isAfter(task.getCompletionDate())){
                throw new WebApplicationException("invalid start and end date",400);
            }
            return EntityMapper.INSTANCE.toTaskResponse(task);
        }catch (Exception e){
            log.error("An error occurred while creating task {}",e.getMessage());
            throw new WebApplicationException(e.getMessage(),400);
        }

    }


    @Transactional
    public TaskResponse updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id);

        validateConstant(taskDto);

        if(task == null){
            log.info("Task not found");
            throw new WebApplicationException("Task not found",404);
        }
        if(taskDto.getStatus() != null){
            task.setStatus(taskDto.getStatus());
        }
        if(taskDto.getDescription() != null && !taskDto.getDescription().isBlank()){
            task.setDescription(taskDto.getDescription());
        }
        if(taskDto.getPriority() != null){
            task.setPriority(taskDto.getPriority());
        }
        if(taskDto.getTags() != null  && !taskDto.getTags().isBlank()){
            task.setTags(taskDto.getTags());
        }
        if(taskDto.getAssignee() != null && taskDto.getAssignee() > 0){
            User  assignee = userService.findById(taskDto.getAssignee());
            if(assignee == null){
                throw new WebApplicationException("Assignee not found",404);
            }
            task.setAssignee(assignee);
        }
        if(taskDto.getStartDate() != null){
            task.setStartDate(taskDto.getStartDate());
        }
        if(taskDto.getCompletionDate() != null){
            task.setCompletionDate(taskDto.getCompletionDate());
        }
        task.setModifiedBy(authService.getCurrentUser());

        if(task.getStartDate().isAfter(task.getCompletionDate())){
            log.info("invalid start and end date");
            throw new WebApplicationException("invalid start and end date",400);
        }

        taskRepository.persist(task);

        return EntityMapper.INSTANCE.toTaskResponse(task);

    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public PaginatedResponse<TaskResponse> findByCriteria(String title, String description, TaskPriority priority, TaskStatus status,
                                                          Long assignee, Long creator, LocalDateTime startDate, LocalDateTime completionDate,
                                                          String tags, int offset, int size) {

        StringBuilder queryBuilder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        User createdBy = null;
        User userAssigned = null;
        if(creator != null){
            createdBy = userService.findById(creator);
        }
        if(assignee != null){
            userAssigned  = userService.findById(assignee);
        }


        if (title != null) {
            queryBuilder.append("title like :title and ");
            params.put("title", "%" + title + "%");
        }
        if (description != null) {
            queryBuilder.append("description like :description and ");
            params.put("description", "%" + description + "%");
        }
        if (priority != null) {
            queryBuilder.append("priority = :priority and ");
            params.put("priority", priority);
        }
        if (status != null) {
            queryBuilder.append("status = :status and ");
            params.put("status", status);
        }
        if (userAssigned != null) {
            queryBuilder.append("assignee.id = :assignee or ");
            params.put("assignee_id", assignee);
        }
        if (createdBy != null) {
            queryBuilder.append("creator.id = :creator or ");
            params.put("creator", creator);
        }
        if (startDate != null) {
            queryBuilder.append("startDate >= :startDate and ");
            params.put("startDate", startDate);
        }
        if (completionDate != null) {
            queryBuilder.append("completionDate <= :completionDate and ");
            params.put("completionDate", completionDate);
        }
        if (tags != null) {
            queryBuilder.append("tags like :tags and ");
            params.put("tags", "%" + tags + "%");
        }

        String query = queryBuilder.toString();
        if (!query.isEmpty()) {
            query = query.substring(0, query.length() - 4); // Remove the last 'or '
        } else {
            return getAllTasks(offset, size);
        }

        var panacheQuery = taskRepository.find(query, params);

        List<Task> taskList = panacheQuery.page(Page.of(offset, size)).list();
        List<TaskResponse> taskListResponse = EntityMapper.INSTANCE.toTaskResponseList(taskList);

        long totalItems = panacheQuery.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        return new PaginatedResponse<>(taskListResponse, offset, size, totalItems, totalPages);
    }



    private void validateConstant(TaskDto taskDto){
        if(taskDto.getStatus() != null  && !TaskStatus.isValid(taskDto.getStatus().name())){
            log.info("Invalid tasks status");
            throw new WebApplicationException("invalid task status",400);
        }
        if(taskDto.getPriority() != null && !TaskPriority.isValid(taskDto.getPriority().name())){
            log.info("Invalid tasks priority");
            throw new WebApplicationException("invalid task status",400);
        }
    }

}