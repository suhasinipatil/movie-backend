package com.example.moviebackend.movie;

import com.example.moviebackend.user.UserEntity;
import com.example.moviebackend.user.UserService;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Mock
    private UserService userService;

    @Test
    void getAllMovies(){
        // Given
        String keyword = "test";
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle("test");
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);

        // When
        when(movieRepository.findByKeyword(keyword)).thenReturn(movieEntityList);

        // Then
        List<MovieEntity> result = movieService.getAllMovies(keyword);

        assertEquals(movieEntityList, result);
    }

    @Test
    void getRecommendedMovies(){
        // Given
        String imdbID = "test";
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID("test1");
        movieEntity.setImdbRating("7.8");
        movieEntity.setDirector("test");
        movieEntity.setGenre("test");
        movieEntity.setWriter("test");
        movieEntity.setActors("test");
        movieEntity.setLanguage("test");
        movieEntity.setPoster("test");
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);

        // When
        when(movieRepository.findByImdbID(imdbID)).thenReturn(Optional.of(movieEntity));
        when(movieRepository.findAll()).thenReturn(movieEntityList);

        // Then
        List<MovieEntity> result = movieService.getRecommendedMovies(imdbID);

        assertEquals(movieEntityList, result);
    }

    @Test
    void filterMoviesByYear(){
        // Given
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setYear("test");
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);

        // When
        int currentYear = java.time.Year.now().getValue() - 2;
        when(movieRepository.findByYear(currentYear + "")).thenReturn(movieEntityList);

        // Then
        List<MovieEntity> result = movieService.filterMoviesByYear();

        assertEquals(movieEntityList, result);
    }

   // @Test
    void searchMovies(){
        // Given
        String title = "test";
        SimilarMovieEntity similarMovieEntity = new SimilarMovieEntity();
        similarMovieEntity.setTitle("test");
        List<SimilarMovieEntity> similarMovieEntityList = new ArrayList<>();
        similarMovieEntityList.add(similarMovieEntity);

        MovieService movieServiceSpy = Mockito.spy(movieService);

        // Mock the behavior of dependencies
        when(movieServiceSpy.httpGet("url")).thenReturn(new HttpGet());

    }

    @Test
    void convertRuntime(){
        // Given
        String runtime = "90min";
        String expectedRuntime = "1h 30min";

        // When
        String result = movieService.convertRuntime(runtime);

        // Then
        assertEquals(expectedRuntime, result);
    }

    //@Test
    void getMoviesList(){
    }

    @Test
    void saveMovie(){
        // Given
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID("testImdbID");

        // When
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);

        // Call the method under test
        movieService.saveMovie(movieEntity);

        // Then
        verify(movieRepository, times(1)).save(movieEntity);
    }

    @Test
    void findByImdbID(){
        // Given
        String imdbID = "test";
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID("test");

        // Mock the behavior of dependencies
        when(movieRepository.findByImdbID(imdbID)).thenReturn(Optional.of(movieEntity));

        // Call the method under test
        Optional<MovieEntity> result = Optional.ofNullable(movieService.findByImdbID(imdbID));

        // Then
        assertEquals(Optional.of(movieEntity), result);
        assertEquals(movieEntity.getImdbID(), result.get().getImdbID());
    }

    //@Test
    void saveFavouriteMovie(){
        // Given
        Integer userId = 1;
        String imdbID = "testImdbID";
        UserEntity userEntity = new UserEntity();
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID(imdbID);

        // When
        when(userService.findByUserId(userId)).thenReturn(userEntity);
        when(movieService.findByImdbID(imdbID)).thenReturn(movieEntity);

        // Call the method under test
        movieService.saveFavouriteMovie(imdbID, userId);

        // Then
        verify(userService, times(1)).save(userEntity);
    }

    @Test
    void getFavouriteMovie(){
        // Given
        Integer userId = 1;
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID("test");
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setLstMovie(movieEntityList);

        // Mock dependency
        when(userService.findByUserId(userId)).thenReturn(userEntity);

        // Call the method under test
        List<MovieEntity> result = movieService.getFavouriteMovie(userId);

        // Then
        assertEquals(movieEntityList, result);
    }

    @Test
    void deleteFavouriteMovie(){
        // Given
        String imdbID = "test";
        Integer userId = 1;
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setImdbID("test");
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setLstMovie(movieEntityList);

        // Mock dependencies
        when(userService.findByUserId(userId)).thenReturn(userEntity);
        when(userService.save(userEntity)).thenReturn(userEntity);

        // Call the method under test
        movieService.deleteFavouriteMovie(userId, imdbID);

        // Then
        verify(userService, Mockito.times(1)).save(userEntity);
    }
}