package com.example.moviebackend.movie;

import com.example.moviebackend.movie.dto.FavouriteMovieDTO;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class APIDataFetcherService {

    private final MovieService movieService;

    public APIDataFetcherService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void startFetching() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                // Fetch data from API
                List<SimilarMovieEntity> movies = movieService.searchMovie("gam");

                // Add data to database
               /* for (SimilarMovieEntity movie : movies) {
                    movieService.saveFavouriteMovie(new FavouriteMovieDTO(movie.getTitle(), "username"));
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Schedule the task to run every 10 seconds
       // executorService.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);
    }
}