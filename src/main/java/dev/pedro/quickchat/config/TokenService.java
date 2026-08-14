package dev.pedro.quickchat.config;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import dev.pedro.quickchat.shared.dto.JWTUserData;
import dev.pedro.quickchat.user.User;

@Service
public class TokenService {

    private Algorithm algorithm;

    public TokenService(@Value("${JWT_SECRET:secret}") String secret) {
        algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withClaim("userId",user.getId())
                .withSubject(user.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm)
                .toString();
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            DecodedJWT decode = JWT.require(algorithm)
                                    .build().verify(token);
            return Optional.of( new JWTUserData(decode.getClaim("userId").asString(), decode.getSubject()));
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
