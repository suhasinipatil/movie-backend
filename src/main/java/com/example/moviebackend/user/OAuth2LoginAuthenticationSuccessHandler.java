package com.example.moviebackend.user;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginAuthenticationSuccessHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{
        logger.info("User logged in with OAuth2");
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String name = token.getPrincipal().getAttribute("name");

        logger.info("User logged in with OAuth2: " + name);
        UserEntity userEntity = userRepository.findByUsername(name);
        if (userEntity == null) {
            logger.info("Creating new user: " + name);
            userEntity = new UserEntity();
            userEntity.setUsername(name);
            userRepository.save(userEntity);
        }
        logger.info("Redirecting to frontend");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
