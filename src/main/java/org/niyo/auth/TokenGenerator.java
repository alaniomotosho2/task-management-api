package org.niyo.auth;

import io.smallrye.jwt.build.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.transaction.*;
import org.eclipse.microprofile.config.inject.*;
import org.niyo.user.User;
import org.niyo.user.UserRepository;

import java.time.*;
import java.util.*;

@ApplicationScoped
public class TokenGenerator {

    @ConfigProperty(name = "context.path")
    String CONTEXT_PATH;

    @ConfigProperty(name = "jwt.issuer")
    String ISSUER;

    private final long ACCESS_TOKEN_VALIDITY_PERIOD = 86400; // 24 hours

    private final long REFRESH_TOKEN_VALIDITY_PERIOD = 7776000; // 3 months
    private final Set<String> DEFAULT_ROLE = Collections.singleton("USER");

    @Inject
    UserRepository userRepository;

    @Inject
    RefreshTokenRepository refreshTokenRepository;


    public String generateToken(User user) {
        Instant issuedAt = Instant.now();

        String username = user.getUsername();
        Long userId = user.getId();


        return Jwt.issuer(CONTEXT_PATH)
            .subject(username)
            .audience(CONTEXT_PATH)
            .issuedAt(issuedAt)
            .groups(new HashSet<>(DEFAULT_ROLE))
            .issuer(ISSUER)
            .upn(username)
            .expiresAt(issuedAt.plusSeconds(ACCESS_TOKEN_VALIDITY_PERIOD))
            .claim("userId", userId)
            .sign();
    }

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        var refreshTokenResponse = RefreshToken
            .builder()
            .token(token)
            .createdAt(Instant.now())
            .user(user)
            .expiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_VALIDITY_PERIOD))
            .build();

        RefreshToken ur = new RefreshToken();
        ur.setUser(user);
        ur.setToken(token);
        ur.setCreatedAt(refreshTokenResponse.getCreatedAt());
        ur.setExpiresAt(refreshTokenResponse.getExpiresAt());

        //delete previous refresh token
        deleteRefreshToken(user.getId());

        refreshTokenRepository.persist(ur);


        return refreshTokenResponse;
    }

    private void  deleteRefreshToken(Long id){
            refreshTokenRepository.delete("user.id",id);
    }

}

