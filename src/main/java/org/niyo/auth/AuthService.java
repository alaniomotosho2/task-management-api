package org.niyo.auth;

import io.quarkus.elytron.security.common.*;
import io.quarkus.security.identity.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.transaction.*;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.jwt.*;
import org.niyo.shared.EntityMapper;
import org.niyo.user.User;
import org.niyo.user.UserResponse;
import org.niyo.user.UserService;

import java.time.*;

@ApplicationScoped
public class AuthService {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    TokenGenerator tokenGenerator;

    @Inject
    UserService userService;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Inject
    JsonWebToken jwt;

    @Transactional
    public AccessTokenResponse login(LoginRequest loginRequest){
        User user = userService.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new WebApplicationException("Invalid Login Details", 400));

        if (!BcryptUtil.matches(loginRequest.getPassword(),user.getPassword())) {
            throw new WebApplicationException("Password does not match", 401);
        }

        return createAccessTokenResponse(user);
    }

    @Transactional
    public AccessTokenResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken == null) {
            throw new WebApplicationException("Invalid refresh token",400);
        }
        if (Instant.now().isAfter(refreshToken.getExpiresAt())) {
            throw new WebApplicationException("Refresh token as expired",422);
        }

        refreshTokenRepository.delete(refreshToken);

        return createAccessTokenResponse(refreshToken.getUser());


    }

    @Transactional
    public void revokeToken(String token) {
        RefreshToken r =refreshTokenRepository.findByToken(token);
        refreshTokenRepository.delete(r);
    }

    private AccessTokenResponse createAccessTokenResponse(User user) {
        String accessToken = tokenGenerator.generateToken(user);
        String refreshToken = tokenGenerator.generateRefreshToken(user).getToken();
        UserResponse ur = EntityMapper.INSTANCE.toUserResponse(user);
        return new AccessTokenResponse(accessToken, refreshToken,ur);
    }

    public User getCurrentUser(){
        if(securityIdentity.isAnonymous()){
            throw new WebApplicationException("Unauthorized",401);
        }

        return userService.findByUsername(securityIdentity.getPrincipal().getName())
            .orElseThrow(()-> new WebApplicationException("Unauthorized"));
    }

}
