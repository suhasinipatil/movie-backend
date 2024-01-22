package com.example.moviebackend.user;

import com.example.moviebackend.movie.MovieService;
import com.example.moviebackend.security.jwt.JWTService;
import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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