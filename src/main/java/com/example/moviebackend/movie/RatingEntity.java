package com.example.moviebackend.movie;

import com.example.moviebackend.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ratings")
@Getter
@Setter
public class RatingEntity extends BaseEntity {

    @Column(name = "Source")
    @JsonProperty("Source")
    private String source;

    @Column(name = "Value")
    @JsonProperty("Value")
    private String value;
}