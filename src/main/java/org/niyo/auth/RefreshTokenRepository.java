package org.niyo.auth;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepositoryBase<RefreshToken,Long> {

    public RefreshToken findByToken(String token) {
        return find("token", token).firstResult();
    }

}