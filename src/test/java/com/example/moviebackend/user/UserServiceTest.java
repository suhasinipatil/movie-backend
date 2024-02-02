package com.example.moviebackend.user;

import com.example.moviebackend.security.jwt.JWTService;
import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // This is needed to use Mockito annotations
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser(){
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testUser");
        createUserDTO.setPassword("testPassword");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("testUser");
        expectedResponse.setToken("testToken");

        // Mock the behavior of dependencies
        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(null);
        when(modelMapper.map(createUserDTO, UserEntity.class)).thenReturn(userEntity);
        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn(userEntity.getPassword());
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserResponseDTO.class)).thenReturn(expectedResponse);
        when(jwtService.createJWT(userEntity.getId())).thenReturn(expectedResponse.getToken());

        // Call the method under test
        UserResponseDTO response = userService.createUser(createUserDTO);

        // Then
        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getToken(), response.getToken());
    }

    @Test
    void loginUser(){
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setPassword("testPassword");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("testUser");
        expectedResponse.setToken("testToken");

        // Mock the behavior of dependencies
        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(userEntity);
        when(passwordEncoder.matches(userEntity.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(modelMapper.map(userEntity, UserResponseDTO.class)).thenReturn(expectedResponse);
        when(jwtService.createJWT(userEntity.getId())).thenReturn(expectedResponse.getToken());

        // Call the method under test
        UserResponseDTO response = userService.loginUser(userEntity);

        // Then
        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getToken(), response.getToken());
    }

    @Test
    void findByUserId(){
        // Given
        Integer userId = 1;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        // Mock the behavior of dependencies
        when(userRepository.findByUserId(userId)).thenReturn(userEntity);

        // Call the method under test
        UserEntity response = userService.findByUserId(userId);

        // Then
        assertEquals(userEntity.getUsername(), response.getUsername());
    }

    @Test
    void findByUsername(){
        // Given
        String username = "testUser";

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);

        // Mock the behavior of dependencies
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        // Call the method under test
        UserEntity response = userService.findByUsername(username);

        // Then
        assertEquals(userEntity.getUsername(), response.getUsername());
    }

    @Test
    void save(){
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        // Mock the behavior of dependencies
        when(userRepository.saveAndFlush(userEntity)).thenReturn(userEntity);

        // Call the method under test
        UserEntity response = userService.save(userEntity);

        // Then
        assertEquals(userEntity.getUsername(), response.getUsername());
    }

    @Test
    void exchangeCodeForToken(){
        // Given
        String code = "testCode";

        String expectedResponse = "testToken";

        // Mock the behavior of dependencies
        when(userService.exchangeCodeForToken(code)).thenReturn(expectedResponse);

        // Call the method under test
        String response = userService.exchangeCodeForToken(code);

        // Then
        assertEquals(expectedResponse, response);
    }

    @Test
    void getUserInfoFromGoogle(){
        // Given
        String accessToken = "mockAccessToken";

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("testUser");
        expectedResponse.setToken("testToken");

        // Mock the behavior of dependencies
        when(userService.getUserInfoFromGoogle(accessToken)).thenReturn(expectedResponse);

        // Call the method under test
        UserResponseDTO response = userService.getUserInfoFromGoogle(accessToken);

        // Then
        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getToken(), response.getToken());
    }
}