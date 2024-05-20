package org.niyo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niyo.auth.RefreshToken;
import org.niyo.auth.RefreshTokenRepository;
import org.niyo.constant.TaskPriority;
import org.niyo.constant.TaskStatus;
import org.niyo.task.Task;
import org.niyo.task.TaskRepository;
import org.niyo.task.dto.TaskDto;
import org.niyo.user.User;
import org.niyo.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class TaskResourceTest {

    private String token;

    @Inject
    UserRepository userRepository;
    @Inject
    RefreshTokenRepository refreshTokenRepository;
    @Inject
    TaskRepository taskRepository;
    private String refreshToken;
    private User user3;
    private User user4;
    private Task task;

    @BeforeEach
    @Transactional
    public void setup() {
        user3 = User
            .builder()
            .name("John chuks")
            .username("newuser3")
            .password("$2a$11$Bzw5FHW1xN4y/T1uvzr4nOoio43NtURlICwGRD1wW/a2MzKv2lBz.") //Password@123
            .build();

        user4 = User
            .builder()
            .name("John chuks udo")
            .username("newuser4")
            .password("$2a$11$Bzw5FHW1xN4y/T1uvzr4nOoio43NtURlICwGRD1wW/a2MzKv2lBz.")//Password@123
            .build();

        userRepository.persist(user3);
        userRepository.persist(user4);

        User user = userRepository.find("username","newuser3").firstResult(); // this is expected to return value

        refreshToken = UUID.randomUUID().toString();
        var refreshTokenEntity = RefreshToken
            .builder()
            .token(refreshToken)
            .user(user)
            .expiresAt(Instant.now().plusSeconds(9000))
            .build();

        refreshTokenRepository.deleteAll();

        refreshTokenRepository.persist(refreshTokenEntity);

        token = generateJwtToken(); // Use this JWT token for authenticated requests

        task = Task.builder().assignee(user3).startDate(LocalDateTime.now())
            .completionDate(LocalDateTime.now().plusMonths(2)).creator(user3)
            .title("Rust Language").description("learning a language at a time")
            .priority(TaskPriority.HIGH).status(TaskStatus.TODO)
            .build();

        taskRepository.persist(task);
    }

    @AfterEach
    @Transactional
    public void teardown() {
        refreshTokenRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testAuthForGetEndpoint() {
        given()
            .when()
            .get("/tasks")
            .then()
            .statusCode(401); // authorized
    }

    @Test
    public void testGetAllTasksEndpoint() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/tasks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(5)); // Assuming the default size is 5
    }

    @Test
    public void testGetTaskByIdEndpoint() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/tasks/{id}", task.getId())
            .then()
            .statusCode(200)
            .body("creator.username",equalTo("newuser3"))
            .body("title",equalTo("Rust Language"));
    }

    @Test
    public void testCreateTaskEndpoint() {
        // Format LocalDateTime to the specified format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startDate = LocalDateTime.now().format(formatter);
        String completionDate = LocalDateTime.now().plusMonths(2).format(formatter);

        // Directly using JSON string as body
        String taskRequestJson = String.format(
            "{\"assignee\": \"%d\", \"startDate\": \"%s\", \"completionDate\": \"%s\", \"title\": \"Chinese\", \"description\": \"learning a language at a time\", \"priority\": \"LOW\", \"status\": \"TODO\"}",
            user3.getId(), startDate, completionDate
        );

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(taskRequestJson)
            .when()
            .post("/tasks")
            .then()
            .statusCode(201);
    }

    @Test
    public void testUpdateTaskEndpoint() {
        var updateRequest = TaskDto.builder().title("new title").build();

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .when()
            .patch("/tasks/{id}", task.getId())
            .then()
            .statusCode(200);
    }

    @Test
    public void testDeleteTaskEndpoint() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .delete("/tasks/{id}", task.getId())
            .then()
            .statusCode(204);
    }

    @Test
    public void testGetTasksWithFilterEndpoint() {
        given()
            .header("Authorization", "Bearer " + token)
            .queryParam("title", "Test Task")
            .queryParam("priority", "HIGH")
            .when()
            .get("/tasks/filter")
            .then()
            .statusCode(200);
    }

    // Helper method to generate JWT token
    private String generateJwtToken() {

        Instant issuedAt = Instant.now();

        return Jwt.issuer("niyo")
            .subject("newuser3")
            .audience("http://localhost")
            .issuedAt(issuedAt)
            .groups(new HashSet<>(Collections.singleton("USER")))
            .issuer("niyo")
            .upn("newuser3")
            .expiresAt(issuedAt.plusSeconds(86400))
            .claim("userId", user3.getId())
            .sign();
    }
}
