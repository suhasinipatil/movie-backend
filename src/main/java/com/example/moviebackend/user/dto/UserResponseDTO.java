package com.example.moviebackend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    Integer id;
    String username;
    String token;
}
