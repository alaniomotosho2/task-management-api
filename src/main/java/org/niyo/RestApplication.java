package org.niyo;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "Simple Task Management API",
        description = "For sample credentials for authentication use user1 to user20 with the password: Password@123. " +
            "For example:\n" +
            "- Username: user1, Password: Password@123\n" +
            "- Username: user2, Password: Password@123\n\n" +
            "WebSocket Endpoint:\n\n" +
            "Connect to ws://localhost:8080/data with a valid JWT token in the message body or headers.\n\n" +
            "Health Check Endpoint:\n\n" +
            "/q/health/live - The application is up and running.\n" +
            "/q/health/ready - The application is ready to serve requests.\n" +
            "/q/health/started - The application is started.\n" +
            "/q/health - Accumulating all health check procedures in the application.",
        version = "1.0.0"
    ),
    security = @SecurityRequirement(name = "bearerAuth"),
    tags = {
        @Tag(name = "tasks", description = "Operations related to tasks"),
        @Tag(name = "auth", description = "Authentication and authorization operations"),
        @Tag(name = "websocket", description = "WebSocket endpoint for real-time data streaming")
    },
    servers = {
        @Server(url = "/", description = "Default Server")
    }
)
public class RestApplication extends Application {
}
