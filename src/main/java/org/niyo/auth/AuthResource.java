package org.niyo.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;

@Path("/auth")
@SecurityRequirements()
public class AuthResource {

    private AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Token Request, this endpoint is for login, when user sends login credentials, the server return token,refresh token" +
        "and basic user details. if user doe not exists, the server return bad request. if user try to login with" +
        "wrong credentials, the server return unauthorized. the token expires in 24 hours and the client can use " +
        "the refresh token to grab a new token, refresh token expires in 3 months... for test purpose " +
        "here are the sample login credentials user can use to" +
        "do a login.(username:user1, password:Password@123")

    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "successful login, upon successful login, token, refresh token and user details will be sent " +
                    "toto the client, client or user can use the token toto make subsequent http request." +
                    "token expires in 24 hours while refresh token expires in 3 months",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = AccessTokenResponse.class))),
            @APIResponse(
                responseCode = "400",
                description = "When username is not found",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "401",
                description = "When unauthenticated user trying to login with wrong credentials",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid @NotNull LoginRequest loginCredentials){
        var response = authService.login(loginCredentials);
        return Response.ok(response).build();
    }



    @Operation(summary = "Refresh a Token. Refresh token is used to grab a new token. this is mainly used by client integrating with " +
        "task mgt service. refresh token increase security. Refresh token expires in 3 months")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "successful Refresh token operation",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = AccessTokenResponse.class))),
            @APIResponse(
                responseCode = "404",
                description = "When refresh token is not found",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )

    @POST
    @Path("/token/refresh/{refreshToken}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(@PathParam("refreshToken") String refreshToken) {
        var response = authService.refreshToken(refreshToken);
        return Response.ok(response).build();
    }




    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "204",
                description = "successful revoke of Refresh token operation",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, implementation = AccessTokenResponse.class))),
            @APIResponse(
                responseCode = "404",
                description = "When refresh token is not found",
                content = @Content(mediaType = "application/json")),
            @APIResponse(
                responseCode = "500",
                description = "internal server error",
                content = @Content(mediaType = "application/json")),}
    )
    @Operation(summary = "Revoke a Token. This endpoint explicitly revoke a refresh token. this " +
        "is useful if client feel the previous refresh" +
        "token is compromised for security reason. at every login, refresh token is revoked as well")
    @POST
    @Path("/token/revoke/{refreshToken}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeToken(@PathParam("refreshToken") String refreshToken) {
        authService.revokeToken(refreshToken);

        return Response.noContent().build();
    }

}

