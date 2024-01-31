package com.example.moviebackend.user;

import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // This is needed to use Mockito annotations
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void createUser() throws URISyntaxException{
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testUser");
        createUserDTO.setPassword("testPassword");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("testUser");
        expectedResponse.setToken("testToken");

        // When userService.createUser is called, return the expectedResponse
        when(userService.createUser(createUserDTO)).thenReturn(expectedResponse);

        // Call the method under test
        ResponseEntity<UserResponseDTO> response = userController.createUser(createUserDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse.getUsername(), Objects.requireNonNull(response.getBody()).getUsername());
        assertEquals(expectedResponse.getToken(), response.getBody().getToken());
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

        // When userService.loginUser is called, return the expectedResponse
        when(userService.loginUser(userEntity)).thenReturn(expectedResponse);

        // Call the method under test
        ResponseEntity<UserResponseDTO> response = userController.loginUser(userEntity);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getUsername(), Objects.requireNonNull(response.getBody()).getUsername());
        assertEquals(expectedResponse.getToken(), response.getBody().getToken());
    }

    @Test
    void loginWithGoogle(){
        // Given
        Map<String, String> body = new HashMap<>();
        body.put("access_token", "testToken");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("testUser");

        // When userService.getUserInfoFromGoogle is called, return the expectedResponse
        when(userService.getUserInfoFromGoogle("testToken")).thenReturn(expectedResponse);

        // Call the method under test
        ResponseEntity<?> response = userController.loginWithGoogle(body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void loginWithGoogleForBadRequest(){
        // Given
        Map<String, String> body = new HashMap<>();

        // Call the method under test
        ResponseEntity<?> response = userController.loginWithGoogle(body);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing access token", response.getBody());
    }

    @Test
    void authenticateWithGoogle(){
        // Given
        Map<String, String> body = new HashMap<>();
        body.put("code", "testCode");

        String expectedResponse = "testToken";

        // When userService.exchangeCodeForToken is called, return the expectedResponse
        when(userService.exchangeCodeForToken("testCode")).thenReturn(expectedResponse);

        // Call the method under test
        ResponseEntity<String> response = userController.authenticateWithGoogle(body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void authenticateWithGoogleForBadRequest(){
        // Given
        Map<String, String> body = new HashMap<>();

        // Call the method under test
        ResponseEntity<String> response = userController.authenticateWithGoogle(body);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing authorization code", response.getBody());
    }

   @Test
   void handleUserNotFoundException(){
       // Given
       UserEntity userEntity = new UserEntity();
       userEntity.setUsername("nonExistentUser");

       // When
       Mockito.doThrow(new UserService.UserNotFoundException("nonExistentUser")).when(userService).loginUser(userEntity);

       // Then
       Exception exception = assertThrows(UserService.UserNotFoundException.class, () -> userController.loginUser(userEntity));

       String expectedMessage = "User with username nonExistentUser not found";
       String actualMessage = exception.getMessage();

       assertEquals(expectedMessage, actualMessage);
   }

    @Test
    void handleIllegalArgumentException(){
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testUser");
        createUserDTO.setPassword("testPassword");

        // When
        Mockito.doThrow(new IllegalArgumentException("User already exits for testUser")).when(userService).createUser(createUserDTO);

        // Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.createUser(createUserDTO));

        String expectedMessage = "User already exits for testUser";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}