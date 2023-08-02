package com.example.moviebackend.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SimilarMovieEntity {
    @JsonProperty("Title")
    private String Title;
    @JsonProperty("Year")
    private String Year;
    @JsonProperty("imdbID")
    private String imdbID;
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("Poster")
    private String Poster;
}
