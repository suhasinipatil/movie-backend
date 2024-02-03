package com.example.moviebackend.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTAuthenticationFilterTest {

    @Test
    void testConvert() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        JWTAuthenticationFilter.JWTAuthenticationConverter converter = new JWTAuthenticationFilter.JWTAuthenticationConverter();
        Authentication authentication = converter.convert(request);

        assertInstanceOf(JWTAuthentication.class, authentication);
        assertEquals("token", ((JWTAuthentication) authentication).getCredentials());
    }

}