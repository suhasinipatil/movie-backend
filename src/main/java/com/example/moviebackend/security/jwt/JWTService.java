package com.example.moviebackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * JWTService is a service class responsible for handling JWT related operations.
 */
@Service
public class JWTService {

    // Secret key for JWT signing and verification
    @Value("${jwt.secret}")
    private String secretKey;

    // Algorithm used for JWT signing
    private Algorithm algorithm;

    /**
     * This method is called after the service is constructed.
     * It initializes the signing algorithm with the secret key.
     */
    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    /**
     * This method creates a JWT for a given user ID.
     * The token's issued at time is set to the current time,
     * and the expiry time is set to 15 minutes from now.
     *
     * @param userId the ID of the user for whom the token is created
     * @return the created JWT
     */
    public String createJWT(Integer userId){
        return createJWT(userId,
                new Date(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 15)
        );
    }

    /**
     * This method creates a JWT with the given parameters.
     *
     * @param userID the ID of the user for whom the token is created
     * @param iat the issued at time of the token
     * @param exp the expiry time of the token
     * @return the created JWT
     */
    protected String createJWT(Integer userID, Date iat, Date exp){
        String token = JWT.create()
                .withSubject(userID.toString())
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithm);
        return token;
    }

    /**
     * This method decodes a given JWT and extracts the user ID from it.
     *
     * @param jwt the JWT to decode
     * @return the user ID extracted from the JWT
     */
    public Integer getUserIdFromJWT(String jwt){

        var decodedJWT = JWT.decode(jwt);
        var subject = decodedJWT.getSubject();
        return Integer.parseInt(subject);

    }
}