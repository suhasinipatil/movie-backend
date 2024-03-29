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

    //find movie by keyword
    @Query("SELECT m FROM movies m WHERE m.title LIKE %:keyword%")
    List<MovieEntity> findByKeyword(@Param("keyword") String keyword);

    // filter movies which have come after certain year
    @Query("SELECT m FROM movies m WHERE m.year >= :year")
    List<MovieEntity> findByYear(@Param("year") String year);

}
