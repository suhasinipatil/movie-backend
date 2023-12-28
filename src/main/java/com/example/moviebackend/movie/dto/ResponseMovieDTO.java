package com.example.moviebackend.movie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMovieDTO {
    String Title;
    String Plot;
    String releasedDate;
    String rating;
    String imdbID;
}
