package com.example.moviebackend.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {

    @Query("SELECT m FROM movies m WHERE m.imdbID = :imdbID")
    Optional<MovieEntity> findByImdbID(@Param("imdbID") String imdbID);

}
