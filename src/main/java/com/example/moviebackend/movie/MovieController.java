package com.example.moviebackend.movie;

import com.example.moviebackend.movie.dto.FavouriteMovieDTO;
import com.example.moviebackend.movie.dto.ResponseMovieDTO;
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
    public ResponseEntity<ResponseMovieDTO> getMovies(@PathVariable String title){
        var searchedMovie = movieService.getMovie(title);
        return ResponseEntity.ok(searchedMovie);
    }

    @GetMapping("/{title}/similar")
    public ResponseEntity<List<SimilarMovieEntity>> getSimilarMovies(@PathVariable String title, @RequestParam String genre, @RequestParam String type){
        var searchedMovie = movieService.getSimilarMovies(title, genre, type);
        return ResponseEntity.ok(searchedMovie);
    }

    //save favourite movie
    @PostMapping("/favourite")
    public ResponseEntity<ResponseMovieDTO> saveFavouriteMovie(@RequestBody FavouriteMovieDTO favouriteMovieDTO){
        var savedMovie = movieService.saveFavouriteMovie(favouriteMovieDTO);
        return ResponseEntity.ok(savedMovie);
    }

    //get favourite movie
    @GetMapping("/favourite/{username}")
    public ResponseEntity<List<ResponseMovieDTO>> getFavouriteMovie(@PathVariable String username){
        var savedMovie = movieService.getFavouriteMovie(username);
        return ResponseEntity.ok(savedMovie);
    }

    //delete favourite movie
    @DeleteMapping("/favourite/{username}/{imdbID}")
    public ResponseEntity<String> deleteFavouriteMovie(@PathVariable String username, @PathVariable String imdbID){
        movieService.deleteFavouriteMovie(username, imdbID);
        return ResponseEntity.ok().body("Movie deleted successfully");
    }
}
