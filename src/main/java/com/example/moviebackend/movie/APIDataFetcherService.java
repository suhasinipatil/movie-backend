package com.example.moviebackend.movie;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class is responsible for fetching movie data from an API and saving it to a database.
 * It uses a ScheduledExecutorService to schedule tasks that fetch the data at fixed intervals.
 */

@Service
public class APIDataFetcherService {

    private static final Logger logger = Logger.getLogger(APIDataFetcherService.class.getName());
    private final MovieService movieService;

    private Iterator<String> iterator;

    /**
     * Constructs a new APIDataFetcherService with the given MovieService.
     *
     * @param movieService the MovieService to use for fetching and saving movie data
     */
    public APIDataFetcherService(MovieService movieService) {
        this.movieService = movieService;
        List<String> permutations = generatePermutations();
        iterator = permutations.iterator();
    }

    /**
     * Starts fetching movie data from an API using the given permutations.
     * The data is fetched at fixed intervals using a ScheduledExecutorService.
     * The data is saved to a database using the MovieService.
     */
    @Scheduled(fixedRateString = "${fetch.interval}")
    public void startFetching(){
        logger.info("Fetching data from API...");

        try {
            // Check if there is a next permutation
            if(iterator.hasNext()){
                String next = iterator.next();
                logger.info("Fetching data using permutation: " + next);
                // Fetch data from API using the next permutation
                List<MovieEntity> movies = movieService.getMoviesList(next);
                logger.info("Fetched " + movies.size() + " movies");
                // Add data to database
                for(MovieEntity movie : movies){
                    //check if the movie already exists in the database
                    if(movieService.findByImdbID(movie.getImdbID()) != null){
                        continue;
                    }
                    movieService.saveMovie(movie);
                    logger.info("Saved movie: " + movie.getTitle());
                }
            }
        } catch (Exception e) {
            logger.warning("Error fetching data from API: " + e.getMessage());
        }
    }
    /**
     * Generates all possible permutations of a given length using the English alphabet.
     *
     * @return a list of all possible permutations
     */
    public List<String> generatePermutations(){
        int totalCharacters = 26; // 26 letters
        int wordLength = 3;
        List<String> permutations = new ArrayList<>();

        // Generate all possible permutations
        for(int i = 0; i < Math.pow(totalCharacters, wordLength); i++){
            StringBuilder permutation = new StringBuilder();
            int temp = i;

            // Construct the permutation by converting the number to base 'totalCharacters'
            for(int j = 0; j < wordLength; j++){
                char character = (char) ('a' + temp % totalCharacters);
                permutation.insert(0, character);
                temp /= totalCharacters;
            }

            permutations.add(permutation.toString());
        }

        return permutations;
    }
}