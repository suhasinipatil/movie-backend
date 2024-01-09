package com.example.moviebackend.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * This class implements the Authentication interface from Spring Security.
 * It represents an authentication token in the form of a JWT (JSON Web Token).
 * The token and userId are stored as instance variables.
 */
public class JWTAuthentication implements Authentication {

    private  String token;
    private Integer userId;

    /**
     * Constructor for the JWTAuthentication class.
     * It sets the token instance variable.
     *
     * @param token JWT token
     */
    public JWTAuthentication(String token) {
        this.token = token;
    }

    /**
     * This method is used to set the userId instance variable.
     *
     * @param userId User ID
     */
    public void setUserId(Integer userId){
        this.userId = userId;
    }

    /**
     * This method is used to get the authorities for this authentication.
     * In this implementation, it returns null.
     *
     * @return Collection of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * This method is used to get the credentials for this authentication.
     * In this implementation, it returns the token.
     *
     * @return JWT token
     */
    @Override
    public String getCredentials() {
        return token;
    }

    /**
     * This method is used to get the details for this authentication.
     * In this implementation, it returns null.
     *
     * @return Object
     */
    @Override
    public Object getDetails() {
        return null;
    }

    /**
     * This method is used to get the principal for this authentication.
     * In this implementation, it returns the userId.
     *
     * @return User ID
     */
    @Override
    public Integer getPrincipal() {
        return userId;
    }

    /**
     * This method is used to check if this authentication is authenticated.
     * In this implementation, it checks if the userId is not null.
     *
     * @return boolean
     */
    @Override
    public boolean isAuthenticated() {
        return userId != null;
    }

    /**
     * This method is used to set if this authentication is authenticated.
     * In this implementation, it does nothing.
     *
     * @param isAuthenticated boolean
     * @throws IllegalArgumentException
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    /**
     * This method is used to get the name for this authentication.
     * In this implementation, it returns null.
     *
     * @return String
     */
    @Override
    public String getName() {
        return null;
    }
}