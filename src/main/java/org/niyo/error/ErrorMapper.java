package org.niyo.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Provider
@Slf4j
public class ErrorMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        log.error("",exception);

        if(exception instanceof WebApplicationException webApplicationException){

            return Response.status(webApplicationException.getResponse().getStatus())
                .entity(
                    ErrorResponse.builder()
                        .code(webApplicationException.getResponse().getStatus())
                        .error(webApplicationException.getMessage())
                        .build())
                .build();
        }


        return Response.status(INTERNAL_SERVER_ERROR)
            .entity(
                ErrorResponse.builder()
                    .code(INTERNAL_SERVER_ERROR.getStatusCode())
                    .error(exception.getMessage())
                    .build())
            .build();
    }
}