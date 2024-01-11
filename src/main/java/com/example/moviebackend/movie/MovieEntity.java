package com.example.moviebackend.movie;

import com.example.moviebackend.common.BaseEntity;
import com.example.moviebackend.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/*@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieEntity extends BaseEntity {
    @JsonProperty("Title")
    String title;
    @JsonProperty("Plot")
    String Plot;
    @JsonProperty("Released")
    String releasedDate;
    @JsonProperty("imdbRating")
    Double imdbRating;
    @JsonProperty("imdbID")
    String imdbID;
}*/


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
@Entity(name = "movies")
@Getter
@Setter
public class MovieEntity {
    @Id
    @Column(name = "imdbID")
    @JsonProperty("imdbID")
    private String imdbID;

    @Column(name = "Title")
    @JsonProperty("Title")
    private String title;

    @Column(name = "Year")
    @JsonProperty("Year")
    private String year;

    @Column(name = "Rated")
    @JsonProperty("Rated")
    private String rated;

    @Column(name = "Released")
    @JsonProperty("Released")
    private String released;

    @Column(name = "Runtime")
    @JsonProperty("Runtime")
    private String runtime;

    @Column(name = "Genre")
    @JsonProperty("Genre")
    private String genre;

    @Column(name = "Director")
    @JsonProperty("Director")
    private String director;

    @Column(name = "Writer")
    @JsonProperty("Writer")
    private String writer;

    @Column(name = "Actors")
    @JsonProperty("Actors")
    private String actors;

    @Column(name = "Plot")
    @JsonProperty("Plot")
    private String plot;

    @Column(name = "Language")
    @JsonProperty("Language")
    private String language;

    @Column(name = "Country")
    @JsonProperty("Country")
    private String country;

    @Column(name = "Awards")
    @JsonProperty("Awards")
    private String awards;

    @Column(name = "Poster")
    @JsonProperty("Poster")
    private String poster;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "movie_id")
    @JsonProperty("Ratings")
    private List<RatingEntity> ratings;

    @Column(name = "Metascore")
    @JsonProperty("Metascore")
    private String metascore;

    @Column(name = "imdbRating")
    @JsonProperty("imdbRating")
    private String imdbRating;

    @Column(name = "imdbVotes")
    @JsonProperty("imdbVotes")
    private String imdbVotes;

    @Column(name = "Type")
    @JsonProperty("Type")
    private String type;

    @Column(name = "DVD")
    @JsonProperty("DVD")
    private String dvd;

    @Column(name = "BoxOffice")
    @JsonProperty("BoxOffice")
    private String boxOffice;

    @Column(name = "Production")
    @JsonProperty("Production")
    private String production;

    @Column(name = "Website")
    @JsonProperty("Website")
    private String website;

    @Column(name = "totalSeasons")
    @JsonProperty("totalSeasons")
    private String totalSeasons;

    @Column(name = "Response")
    @JsonProperty("Response")
    private String response;
}