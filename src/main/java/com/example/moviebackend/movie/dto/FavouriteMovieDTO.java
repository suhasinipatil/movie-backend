package com.example.moviebackend.movie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FavouriteMovieDTO {
    @JsonProperty("Title")
    String title;
    @JsonProperty("Plot")
    String Plot;
    @JsonProperty("releasedDate")
    String releasedDate;
    @JsonProperty("imdbRating")
    String imdbRating;
    @JsonProperty("imdbID")
    String imdbID;
    @JsonProperty("username")
    String username;
}
