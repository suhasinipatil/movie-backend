package com.example.moviebackend.movie;

import com.example.moviebackend.common.BaseEntity;
import com.example.moviebackend.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity(name = "movies")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieEntity extends BaseEntity {
    @JsonProperty("Title")
    String title;
    @JsonProperty("Plot")
    String synopsis;
    @JsonProperty("Released")
    String releasedDate;
    @JsonProperty("imdbRating")
    Double rating;
    @JsonProperty("imdbID")
    String imdbID;
}
