package com.example.moviebackend.user;


import com.example.moviebackend.common.BaseEntity;
import com.example.moviebackend.movie.MovieEntity;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    String username;
    String password;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "movie_likes",
            joinColumns = @javax.persistence.JoinColumn(name = "movie_id"),
            inverseJoinColumns = @javax.persistence.JoinColumn(name = "user_id")
    )
    List<MovieEntity> lstMovie;
}
