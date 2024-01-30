package com.example.moviebackend.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class is responsible for fetching movie data from an API and saving it to a database.
 * It uses a ScheduledExecutorService to schedule tasks that fetch the data at fixed intervals.
 */

@Service
public class APIDataFetcherService {

    private static final Logger logger = Logger.getLogger(APIDataFetcherService.class.getName());

    @Autowired
    private MovieService movieService;

    private final Iterator<String> iterator;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;


    /**
     * Constructs a new APIDataFetcherService with the given MovieService.
     *
     * @param movieService the MovieService to use for fetching and saving movie data
     */
    public APIDataFetcherService(MovieService movieService) {
        this.movieService = movieService;
        List<String> permutations = generatePermutations();
        String lastPermutation = PermutationTracker.readLastPermutation();
        logger.info("Last permutation used: " + lastPermutation);
        int startIndex = lastPermutation != null ? permutations.indexOf(lastPermutation) + 1 : 0;
        iterator = permutations.listIterator(startIndex);
        logger.info("Starting from permutation: " + permutations.get(startIndex));
    }
    @PostConstruct
    public void startFetching() {
        int fetchInterval = 6000; // replace with your fetch interval
        this.scheduledFuture = this.scheduler.scheduleAtFixedRate(this::fetchData, 6000, fetchInterval, TimeUnit.MINUTES);
    }

    private void fetchData() {
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
                // Save the last permutation that was used
                PermutationTracker.writeLastPermutation(next);
            }
        } catch (MovieService.RequestLimitReachedException e) {
            this.scheduledFuture.cancel(true); // This will stop the scheduled task
            logger.info("Request limit reached. Stopping scheduled task.");
        } catch (Exception e) {
            logger.severe("Error fetching data from API: " + e.getMessage());
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
        logger.info("Generating permutations...");
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
        logger.info("Generated " + permutations.size() + " permutations");

        return permutations;
    }
}