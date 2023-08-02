package com.example.moviebackend.movie;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/movies")
public class MovieController {

    public final MovieService movieService;

    public MovieController(MovieService movieService){
        this.movieService = movieService;
    }

    @GetMapping("/{title}")
    public ResponseEntity<MovieEntity> getMovies(@PathVariable String title){
        var searchedMovie = movieService.getMovie(title);
        return ResponseEntity.ok(searchedMovie);
    }

    @GetMapping("/{title}/similar")
    public ResponseEntity<List<SimilarMovieEntity>> getSimilarMovies(@PathVariable String title, @RequestParam String genre, @RequestParam String type){
        var searchedMovie = movieService.getSimilarMovies(title, genre, type);
        return ResponseEntity.ok(searchedMovie);
    }
}
