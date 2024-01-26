package com.example.moviebackend.user;

import com.example.moviebackend.security.jwt.JWTService;
import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * This class is responsible for managing users in the application.
 * It provides methods for creating and logging in users, as well as finding users by their username.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public final UserRepository userRepository;
    public final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String secret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @PostConstruct
    public void init(){
        logger.info("clientId: " + clientId);
        logger.info("secret: " + secret);
        logger.info("redirectUri: " + redirectUri);
    }

    /**
     * Constructor for the UserService class.
     *
     * @param userRepository UserRepository
     * @param modelMapper ModelMapper
     * @param passwordEncoder PasswordEncoder
     * @param jwtService JWTService
     */
    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * This method is used to create a new user.
     * It first checks if a user with the same username already exists.
     * If not, it creates a new user, saves it in the database, and returns a UserResponseDTO object.
     *
     * @param createUserDTO CreateUserDTO
     * @return UserResponseDTO
     */
    public UserResponseDTO createUser(CreateUserDTO createUserDTO){
        var userEntity = userRepository.findByUsername(createUserDTO.getUsername());
        if(userEntity != null){
            throw new UserAlreadyExitsException(userEntity.getUsername());
        }
        var newUserEntity = modelMapper.map(createUserDTO, UserEntity.class);
        newUserEntity.setPassword(passwordEncoder.encode(newUserEntity.getPassword()));
        var savedUser = userRepository.save(newUserEntity);
        var userResponseDTO = modelMapper.map(savedUser, UserResponseDTO.class);
        userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
        return userResponseDTO;
    }

    /**
     * This method is used to log in a user.
     * It first checks if a user with the given username exists.
     * If the user exists and the password matches, it returns a UserResponseDTO object.
     *
     * @param loginUserDTO UserEntity
     * @return UserResponseDTO
     */
    public UserResponseDTO loginUser(UserEntity loginUserDTO){
        var userEntity = userRepository.findByUsername(loginUserDTO.getUsername());
        if(userEntity == null){
            throw new UserNotFoundException(loginUserDTO.getUsername());
        }
        var passMatch = passwordEncoder.matches(loginUserDTO.getPassword(), userEntity.getPassword());
        if(!passMatch){
            throw new IllegalArgumentException("Incorrect password");
        }
        var userResponseDTO = modelMapper.map(userEntity, UserResponseDTO.class);
        userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
        return userResponseDTO;
    }

    /**
     * This method is used to find a user by their id.
     * If the user does not exist, it throws a UserNotFoundException.
     *
     * @param userId Integer
     * @return UserEntity
     */
    public UserEntity findByUserId(Integer userId){
        var userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UserNotFoundException(userId);
        }
        return userEntity;
    }


    /**
     * This method is used to find a user by their username.
     * If the user does not exist, it throws a UserNotFoundException.
     *
     * @param username String
     * @return UserEntity
     */
    public UserEntity findByUsername(String username){
        var userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            throw new UserNotFoundException(username);
        }
        return userEntity;
    }

    /**
     * This method is used to save a user entity.
     *
     * @param user UserEntity
     * @return UserEntity
     */
    public UserEntity save(UserEntity user) {
        UserEntity savedUser = userRepository.saveAndFlush(user);
        return savedUser;
    }


    public String exchangeCodeForToken(String code) {
        logger.info("code: " + code);
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(code);
        logger.info("Request: " + request.toString());
        ResponseEntity<String> response = restTemplate.exchange("https://oauth2.googleapis.com/token", HttpMethod.POST, request, String.class);
        logger.info("Response: " + response.toString());

        // Parse the response body to extract the access token
        // This depends on the response format. Here's a basic example if the response is JSON:
        JSONObject jsonObject = (JSONObject) JSONValue.parse(response.getBody());
        int expiresInSec = (int) jsonObject.get("expires_in");
        int expiresInMin = expiresInSec / 60;
        logger.info("Token expires in: " + expiresInMin + " minutes");
        return (String) jsonObject.get("access_token");
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", secret);
        map.add("code", code);
        map.add("redirect_uri", redirectUri);
        map.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return request;
    }

    /**
     * This class is used to represent an exception that is thrown when a user is not found.
     */
    public static class UserNotFoundException extends IllegalArgumentException{
        public UserNotFoundException(Integer userId) {
            super("User with id " + userId + " is not found");
            logger.info("User with id " + userId + " is not found");

        }

        public UserNotFoundException(String username) {
            super("User with username " + username + " not found");
            logger.info("User with username " + username + " not found");
        }
    }

    /**
     * This class is used to represent an exception that is thrown when a user already exists.
     */
    public static class UserAlreadyExitsException extends IllegalArgumentException{
        public UserAlreadyExitsException(String username) {

            super("User already exits for " + username);
            logger.info("User already exits for " + username);
        }
    }
}