package com.example.moviebackend.movie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    MovieService movieService;

    @InjectMocks
    MovieController movieController;

    @Test
    void searchMovies(){
        // Given
        String title = "testTitle";
        MovieEntity movie = new MovieEntity();
        movie.setTitle("testTitle");
        List<MovieEntity> movieEntity = new ArrayList<>();
        movieEntity.add(movie);

        // When
        when(movieService.getAllMovies(title)).thenReturn(movieEntity);

        // Call the method under test
        ResponseEntity<List<MovieEntity>> result = movieController.searchMovies(title);

        // Then
        assertEquals(movieEntity, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("testTitle", result.getBody().get(0).getTitle());
    }

    @Test
    void filterMoviesByYear(){
        // Given
        MovieEntity movie = new MovieEntity();
        movie.setTitle("testTitle");
        List<MovieEntity> movieEntity = new ArrayList<>();
        movieEntity.add(movie);

        // When
        when(movieService.filterMoviesByYear()).thenReturn(movieEntity);

        // Call the method under test
        ResponseEntity<List<MovieEntity>> result = movieController.filterMoviesByYear();

        // Then
        assertEquals(movieEntity, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getMovie(){
        // Given
        String imdbID = "testImdbID";
        MovieEntity movie = new MovieEntity();
        movie.setImdbID("testImdbID");
        List<MovieEntity> movieEntity = new ArrayList<>();
        movieEntity.add(movie);

        // When
        when(movieService.getRecommendedMovies(imdbID)).thenReturn(movieEntity);

        // Call the method under test
        ResponseEntity<List<MovieEntity>> result = movieController.getMovie(imdbID);

        // Then
        assertEquals(movieEntity, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("testImdbID", result.getBody().get(0).getImdbID());
    }

    @Test
    void saveFavouriteMovie(){
        // Given
        String imdbID = "testImdbID";
        Integer userId = 1;

        // When
        doNothing().when(movieService).saveFavouriteMovie(imdbID, userId);

        // Call the method under test
        ResponseEntity<String> result = movieController.saveFavouriteMovie(imdbID, userId);

        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Movie saved successfully", result.getBody());
        verify(movieService, times(1)).saveFavouriteMovie(imdbID, userId);
    }

    @Test
    void getFavouriteMovie(){
        // Given
        Integer userId = 1;
        MovieEntity movie = new MovieEntity();
        movie.setTitle("testTitle");
        List<MovieEntity> movieEntity = new ArrayList<>();
        movieEntity.add(movie);

        // When
        when(movieService.getFavouriteMovie(userId)).thenReturn(movieEntity);

        // Call the method under test
        ResponseEntity<List<MovieEntity>> result = movieController.getFavouriteMovie(userId);

        // Then
        assertEquals(movieEntity, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("testTitle", result.getBody().get(0).getTitle());
    }

    @Test
    void deleteFavouriteMovie(){
        // Given
        String imdbID = "testImdbID";
        Integer userId = 1;

        // When
        doNothing().when(movieService).deleteFavouriteMovie(userId, imdbID);

        // Call the method under test
        ResponseEntity<String> result = movieController.deleteFavouriteMovie(userId, imdbID);

        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Movie deleted successfully", result.getBody());
        verify(movieService, times(1)).deleteFavouriteMovie(userId, imdbID);
    }

}