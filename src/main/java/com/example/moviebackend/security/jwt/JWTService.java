package com.example.moviebackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;
    private Algorithm algorithm;
    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }
    public String createJWT(Integer userId){
        return createJWT(userId,
                new Date(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)
                );
    }

    protected String createJWT(Integer userID, Date iat, Date exp){
        String token = JWT.create()
                .withSubject(userID.toString())
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithm);
        return token;
    }

    public Integer getUserIdFromJWT(String jwt){

        var decodedJWT = JWT.decode(jwt);
        var subject = decodedJWT.getSubject();
        return Integer.parseInt(subject);

    }


}
