package com.example.moviebackend.security.jwt;

import com.example.moviebackend.movie.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * This class extends the AuthenticationFilter class and is responsible for handling JWT authentication.
 * It uses a custom AuthenticationManager and AuthenticationConverter to handle JWT tokens.
 */
public class JWTAuthenticationFilter extends AuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    /**
     * Constructor for the JWTAuthenticationFilter class.
     * It sets a custom AuthenticationManager and AuthenticationConverter.
     * It also sets a success handler that sets the authentication in the SecurityContextHolder.
     */
    public JWTAuthenticationFilter() {
        super(new JWTAuthenticationManager(), new JWTAuthenticationConverter());

        setSuccessHandler(((request, response, authentication) -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }));

        logger.info("JWTAuthenticationFilter initialized");
    }

    /**
     * This class implements the AuthenticationConverter interface.
     * It is responsible for converting a HttpServletRequest into an Authentication object.
     */
    static class JWTAuthenticationConverter implements AuthenticationConverter{

        /**
         * This method is used to convert a HttpServletRequest into an Authentication object.
         * It checks if the request has an "Authorization" header and if it does, it creates a new JWTAuthentication object.
         *
         * @param request HttpServletRequest
         * @return Authentication
         */
        @Override
        public Authentication convert(HttpServletRequest request) {
            if(request.getHeader("Authorization") != null){
                var splitValue = request.getHeader("Authorization").split(" ");

                if(splitValue.length != 2){
                    return null;
                }
                String token = splitValue[1];
                return new JWTAuthentication(token);
            }
            return null;
        }
    }

    /**
     * This class implements the AuthenticationManager interface.
     * It is responsible for authenticating a JWTAuthentication object.
     */
    static class JWTAuthenticationManager implements AuthenticationManager{
        private JWTService jwtService = new JWTService();

        /**
         * This method is used to authenticate a JWTAuthentication object.
         * It checks if the authentication is a JWTAuthentication and if it is, it sets the userId in the JWTAuthentication.
         *
         * @param authentication Authentication
         * @return Authentication
         * @throws AuthenticationException if an error occurs
         */
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            if (authentication instanceof JWTAuthentication) {
                JWTAuthentication jwtAuthentication = (JWTAuthentication) authentication;
                String token = jwtAuthentication.getCredentials();

                if (token != null) {
                    var userId = jwtService.getUserIdFromJWT(token);
                    jwtAuthentication.setUserId(userId);

                    return jwtAuthentication;
                }
            }

            return null;
        }
    }
}