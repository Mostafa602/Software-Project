package com.lms.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.lms.domain.model.user.Roles;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    //    @Value("${jwt.secret}")
    private final String JWTSECRET = "ElGayar";

    //    @Value("${jwt.expiration}")
    private final long JWTEXPIRATION = 60 * 60 * 24 * 7 * 1000;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(JWTSECRET);
    }

    public String generateToken(Long id, Roles role) {
        return JWT.create()
                .withSubject(id.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTEXPIRATION))
                .withClaim("role", role.name())
                .sign(getAlgorithm());
    }


    public Long getId(String token) {
        return Long.parseLong( decodeToken(token).getSubject());
    }

    public Roles getRole(String token) throws IllegalArgumentException{
        return Roles.valueOf(decodeToken(token).getClaim("role").asString());
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm()).build();
            verifier.verify(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }
}

