package com.example.moviebackend.user;

import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO createUserDTO) throws URISyntaxException {
        var createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.created(new URI("/users/")).body(createdUser);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody UserEntity loginUserDTO)
    {
        var savedUser = userService.loginUser(loginUserDTO);
        return ResponseEntity.ok(savedUser);
    }

    @ExceptionHandler(UserService.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserService.UserNotFoundException ex){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
