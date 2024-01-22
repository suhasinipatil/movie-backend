package com.example.moviebackend.movie;

import com.example.moviebackend.common.dto.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<MovieEntity>> searchMovies(@PathVariable String title){
        var searchedMovie = movieService.getAllMovies(title);
        return ResponseEntity.ok(searchedMovie);
    }

    @GetMapping("/year")
    public ResponseEntity<List<MovieEntity>> filterMoviesByYear(){
        var searchedMovie = movieService.filterMoviesByYear();
        return ResponseEntity.ok(searchedMovie);
    }

    @GetMapping("/similar/{imdbID}")
    public ResponseEntity<List<MovieEntity>> getMovie(@PathVariable String imdbID){
        var searchedMovie = movieService.getRecommendedMovies(imdbID);
        return ResponseEntity.ok(searchedMovie);
    }

    //save favourite movie
    @PostMapping("/favourite/{imdbID}")
    public ResponseEntity<String> saveFavouriteMovie(@PathVariable String imdbID, @AuthenticationPrincipal Integer userId){
        movieService.saveFavouriteMovie(imdbID, userId);
        return ResponseEntity.ok().body("Movie saved successfully");
    }

    //get favourite movie
    @GetMapping("/favourite")
    public ResponseEntity<List<MovieEntity>> getFavouriteMovie(@AuthenticationPrincipal Integer userId){
        var savedMovie = movieService.getFavouriteMovie(userId);
        return ResponseEntity.ok(savedMovie);
    }

    //delete favourite movie
    @DeleteMapping("/favourite/{imdbID}")
    public ResponseEntity<String> deleteFavouriteMovie(@AuthenticationPrincipal Integer userId, @PathVariable String imdbID){
        movieService.deleteFavouriteMovie(userId, imdbID);
        return ResponseEntity.ok().body("Movie deleted successfully");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException e) {
        // Log the exception (optional)
        // logger.error("Unexpected error", e);

        // Return a ResponseEntity with a custom error message and HTTP status code
        var errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
}
