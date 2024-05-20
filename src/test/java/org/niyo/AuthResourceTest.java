package org.niyo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niyo.auth.LoginRequest;
import org.niyo.auth.RefreshToken;
import org.niyo.auth.RefreshTokenRepository;
import org.niyo.user.User;
import org.niyo.user.UserRepository;

import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class AuthResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    RefreshTokenRepository refreshTokenRepository;
    private String refreshToken;

    @BeforeEach
    @Transactional
    public void setup() {
        var user1 = User
            .builder()
            .name("John chuks")
            .username("newuser1")
            .password("$2a$11$Bzw5FHW1xN4y/T1uvzr4nOoio43NtURlICwGRD1wW/a2MzKv2lBz.") //Password@123
            .build();

        var user2 = User
            .builder()
            .name("John chuks udo")
            .username("newuser2")
            .password("$2a$11$Bzw5FHW1xN4y/T1uvzr4nOoio43NtURlICwGRD1wW/a2MzKv2lBz.")//Password@123
            .build();

        userRepository.persist(user1);
        userRepository.persist(user2);

        User user = userRepository.find("username","newuser1").firstResult(); // this is expected to return value

        refreshToken = UUID.randomUUID().toString();
        var refreshTokenEntity = RefreshToken
            .builder()
            .token(refreshToken)
            .user(user)
            .expiresAt(Instant.now().plusSeconds(9000))
            .build();

        refreshTokenRepository.persist(refreshTokenEntity);
    }

    @AfterEach
    @Transactional
    public void teardown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void testLoginEndpoint() {

        var loginRequest = LoginRequest.builder()
            .password("Password@123")
            .username("newuser1").build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token",notNullValue())
            .body("refreshToken",notNullValue())
            .body("user",notNullValue());
    }

    @Test
    public void testInvalidLogin() {

        var loginRequest = LoginRequest.builder().password("Password@123_").username("newuser145").build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(400)
            .body("error",equalTo("Invalid Login Details"));
    }

    @Test
    public void testRefreshTokenEndpoint() {

        given()
            .contentType(ContentType.ANY)
            .pathParam("refreshToken", refreshToken)
            .when()
            .post("/auth/token/refresh/{refreshToken}")
            .then()
            .statusCode(200)
            .body("token", notNullValue());
    }


    @Test
    public void testRevokeTokenEndpoint() {
        given()
            .contentType(ContentType.ANY)
            .when()
            .post("/auth/token/revoke/{refreshToken}", refreshToken)
            .then()
            .statusCode(204);
    }
}

