package com.example.moviebackend.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthenticationTest {

    @Test
    void testGetCredentials(){
        JWTAuthentication jwtAuthentication = new JWTAuthentication("testToken");
        assertEquals("testToken", jwtAuthentication.getCredentials());
    }

    @Test
    void getPrincipal(){
        JWTAuthentication jwtAuthentication = new JWTAuthentication("testToken");
        jwtAuthentication.setUserId(1);
        assertNotNull(jwtAuthentication.getPrincipal());
    }

    @Test
    void isAuthenticated(){
        JWTAuthentication jwtAuthentication = new JWTAuthentication("testToken");
        jwtAuthentication.setUserId(1);
        assertTrue(jwtAuthentication.isAuthenticated());
    }
}