package com.example.moviebackend.movie.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FavouriteMovieDTO {
    String title;
    String synopsis;
    Date releaseDate;
    Double ratings;
}
