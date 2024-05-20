package org.niyo.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.WebApplicationException;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodeValidator;
import org.jose4j.jwt.consumer.InvalidJwtException;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import org.jose4j.jwt.consumer.JwtContext;

@ApplicationScoped
@Alternative
@Priority(1)
public class TokenVerifier extends JWTCallerPrincipalFactory {

    private final List<ErrorCodeValidator.Error> details = Collections.emptyList();
    private JwtContext jwtContext;

    @Override
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
        try {
            String json = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            var parsedToken =   new DefaultJWTCallerPrincipal(JwtClaims.parse(json));

            return parsedToken;
        } catch (InvalidJwtException ex) {
            throw new WebApplicationException("invalid token",403);
        }
    }
}
