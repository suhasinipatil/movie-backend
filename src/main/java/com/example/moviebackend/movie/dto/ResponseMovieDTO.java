package com.example.moviebackend.movie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMovieDTO {
    String title;
    String synopsis;
    String releaseDate;
    String ratings;
}
