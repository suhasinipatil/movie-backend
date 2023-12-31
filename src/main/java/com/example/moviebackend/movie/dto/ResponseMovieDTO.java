package com.example.moviebackend.movie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ResponseMovieDTO {
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Plot")
    private String plot;
    @JsonProperty("Released")
    private String released;
    @JsonProperty("imdbRating")
    private String imdbRating;
    @JsonProperty("imdbID")
    private String imdbID;
}
