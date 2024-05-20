package org.niyo.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TIME_ZONE = "Africa/Lagos";

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    JsonWebToken principal;


    @Override
    public void filter(ContainerRequestContext requestContext) {
        String bearerAuthToken = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (bearerAuthToken == null || !bearerAuthToken.startsWith("Bearer ")) {
            return;
        }

        // check token expiration
        if(!isTokenExpired(principal.getExpirationTime())){
            abortWithResponse(requestContext, Response.Status.UNAUTHORIZED, "Unauthorized");
            return;
        }

    }

    private boolean isTokenExpired(long timestamp){

        var expireDate = Instant
            .ofEpochSecond(timestamp)
            .atZone(ZoneId.of(TIME_ZONE))
            .toLocalDateTime();

        return expireDate.isAfter(LocalDateTime.now());
    }

    private void abortWithResponse(ContainerRequestContext requestContext, Response.Status status, String message) {
        requestContext.abortWith(Response.status(status).entity(message).build());
    }

}

