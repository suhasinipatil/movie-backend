package com.example.moviebackend.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Test
    void testCreateAndGetUserIdFromJWT() {
        JWTService jwtService = new JWTService();
        jwtService.algorithm = Algorithm.HMAC256("secret");
        String jwt = jwtService.createJWT(1);
        Integer userId = jwtService.getUserIdFromJWT(jwt);
        assertEquals(1, userId);
    }
}