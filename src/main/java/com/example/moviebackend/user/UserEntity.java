package com.example.moviebackend.user;


import com.example.moviebackend.common.BaseEntity;
import com.example.moviebackend.movie.MovieEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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

    @ManyToMany(mappedBy = "lstUser")
    List<MovieEntity> lstMovie;
}
