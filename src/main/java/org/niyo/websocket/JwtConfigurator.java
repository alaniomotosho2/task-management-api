package org.niyo.websocket;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class JwtConfigurator extends ServerEndpointConfig.Configurator {

    @Inject
    JsonWebToken jwt;

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            throw new RuntimeException("Missing Authorization header");
        }

        String token = authHeaders.get(0).replace("Bearer ", "");
        try {
            config.getUserProperties().put("jwt", token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}

