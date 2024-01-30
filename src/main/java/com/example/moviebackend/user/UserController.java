package com.example.moviebackend.user;

import com.example.moviebackend.user.dto.CreateUserDTO;
import com.example.moviebackend.user.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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

    @GetMapping("/users/login/google")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException{
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/users/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body){
        //System.out.println("body: " + body);
        String accessToken = body.get("access_token");
        if(accessToken == null){
            return ResponseEntity.badRequest().body("Missing access token");
        }

        try {
            UserResponseDTO userEntity = userService.getUserInfoFromGoogle(accessToken);
            // Use the userEntity for further actions...
            return ResponseEntity.ok().body(userEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during authentication");
        }
    }

    @PostMapping("/api/auth/google")
    public ResponseEntity<String> authenticateWithGoogle(@RequestBody Map<String, String> body){
       // System.out.println("body: " + body);
        String code = body.get("code");
        if(code == null){
            return ResponseEntity.badRequest().body("Missing authorization code");
        }

        try {
            // You need to implement the method exchangeCodeForToken in your UserService
            String token = userService.exchangeCodeForToken(code);
            System.out.println("token: " + token);
            // Use the token to fetch user info and perform further actions...
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during authentication");
        }
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
