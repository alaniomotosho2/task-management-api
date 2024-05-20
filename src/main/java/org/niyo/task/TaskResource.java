package org.niyo.task;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;
import org.niyo.shared.PaginatedResponse;
import org.niyo.task.dto.TaskDto;
import org.niyo.task.dto.TaskResponse;

import java.time.LocalDateTime;
@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER"})
public class TaskResource {

    @Inject
    TaskService taskService;

    @Operation(summary = "Fetch all tasks, the response is paginated. It may return empty if there is no data." +
        "Default size is 5 and default offset is zero. This endpoint requires authentication. To authenticate, do a login" +
        " from auth resource endpoint and use the token to make" +
        "http call tto this endpoint....here is the sample login credentials....(username:user2,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "Get paginated tasks, this could be empty if there is no data",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = PaginatedResponse.class))),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to fetch tasks",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to access the resources.It" +
                    "could also means that the token is not valid",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )

    @GET
    public Response getAllTasks(@DefaultValue("0") @QueryParam("page") int offset,
                                @DefaultValue("5") @QueryParam("size") int size) {
        var response =  taskService.getAllTasks(offset,size);

        return Response.ok(response)
            .build();
    }



    @Operation(summary = "Fetch a single task. It may return 404 not found if the ID does not exists" +
        "This endpoint requires authentication. To authenticate, do a login" +
        "from auth resource endpoint and use the token to make" +
        "http call tto this endpoint....here is the sample login credentials....(username:user3,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "Get single task",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = TaskResponse.class))),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to fetch single tasks",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to access the resources.",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "404",
                description = "When supplied task ID does not exsts",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @GET
    @Path("/{id}")
    public Response getTaskById(@PathParam("id") Long id) {
        var task = taskService.getTaskById(id);

        return Response.ok(task)
            .build();
    }



    @Operation(summary = "This endpoint create a single task. It may return client or server error." +
        "refer to documentation responses." +
        "This endpoint requires authentication. To authenticate, do a login" +
        "from auth resource endpoint and use the token to make" +
        "http call tto this endpoint....here is the sample login credentials....(username:user3,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "201",
                description = "Task was created successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = TaskResponse.class))),
            @APIResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to create task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to create task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "422",
                description = "When request is not acceptable by the server",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @POST
    public Response createTask(@Valid @NotNull TaskDto task) {
        taskService.createTask(task);
        return Response
            .ok(taskService.createTask(task))
            .status(Response.Status.CREATED)
            .build();
    }



    @Operation(summary = "This endpoint update a single task.It is a patch operation.Client " +
        "only need to change the field that changes. It may return client or server error." +
        "refer to documentation responses." +
        "This endpoint requires authentication. To authenticate, do a login" +
        "from auth resource endpoint and use the token to make" +
        "http call tto this endpoint....here is the sample login credentials....(username:user3,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "Task was updated successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = TaskResponse.class))),
            @APIResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to update task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to update task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "422",
                description = "When request is not acceptable by the server",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @PATCH
    @Path("/{id}")
    public Response updateTask(@PathParam("id") Long id, TaskDto updatedTask) {
        return Response
            .ok(taskService.updateTask(id,updatedTask))
            .build();
    }



    @Operation(summary = "This endpoint delete a single task. It return no content after successful delete operation. It may return client or server error." +
        "refer to documentation responses." +
        "This endpoint requires authentication. To authenticate, do a login" +
        "from auth resource endpoint and use the token to make" +
        "http call to this endpoint....here is the sample login credentials....(username:user3,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "204",
                description = "Task was deleted successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = PaginatedResponse.class))),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to delete task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to delete task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "422",
                description = "When request is not acceptable by the server",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        taskService.deleteTask(id);
        return Response.noContent().build();
    }



    @Operation(summary = "This endpoint filter tasks. It returns a paginated tasks based on client filter criteria." +
        "if no parameter is supplied, then default behaviours is similar to getAllTasks endpoint" +
        "This endpoint requires authentication. To authenticate, do a login" +
        "from auth resource endpoint and use the token to make" +
        "http call tto this endpoint....here is the sample login credentials....(username:user3,password:Password@123)")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "full search of task, this could be empty if there is no match based on provided parameters." +
                    "if no parameter is passed, then it default to GET all tasks. in any case, paginated tasks is returned",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = PaginatedResponse.class))),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to filter task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "403",
                description = "User is unauthenticated but does not have permission to filter task",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "422",
                description = "When request is not acceptable by the server",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @GET
    @Path("/filter")
    public Response getTasks(@QueryParam("title") String title,
                               @QueryParam("description") String description,
                               @QueryParam("priority") TaskPriority priority,
                               @QueryParam("status") TaskStatus status,
                               @QueryParam("assignee") Long assigneeId,
                               @QueryParam("creator") Long creatorId,
                               @QueryParam("startDate") LocalDateTime startDate,
                               @QueryParam("completionDate") LocalDateTime completionDate,
                               @QueryParam("tags") String tags,
                               @DefaultValue("0") @QueryParam("page") int offset,
                               @DefaultValue("5") @QueryParam("size") int size) {

        var searchResult =  taskService.findByCriteria(title, description, priority, status, assigneeId, creatorId, startDate, completionDate, tags,offset,size);

        return Response.ok(searchResult).build();
    }
}

