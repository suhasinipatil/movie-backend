package com.example.moviebackend.movie;

import com.example.moviebackend.movie.dto.FavouriteMovieDTO;
import com.example.moviebackend.movie.dto.ResponseMovieDTO;
import com.example.moviebackend.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * Service class for managing movies.
 */
@Service
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    private final HttpClient client = HttpClients.createDefault();
    private final UserService userService;

    @Value("${api.secret}")
    private String apiKey;
    public static String API_KEY;
    private static String API_URL;

    @PostConstruct
    public void init(){
        API_KEY = this.apiKey;
        API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY;
    }

    /**
     * Constructor for MovieService.
     *
     * @param modelMapper     The ModelMapper instance for mapping between DTOs and entities.
     * @param movieRepository The repository for accessing movie data.
     * @param userService     The service for managing users.
     */
    public MovieService(ModelMapper modelMapper, MovieRepository movieRepository, UserService userService){
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.userService = userService;
    }

    /**
     * Starts fetching movie data from an API using the given permutations.
     * The data is fetched at fixed intervals using a ScheduledExecutorService.
     *
     * @param permutations the permutations to use for fetching the data
     */
    public void startFetching(List<String> permutations){
        logger.info("Starting to fetch movie data");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // Create an iterator for the permutations list
        Iterator<String> iterator = permutations.iterator();
        logger.info("Created iterator");
        Runnable task = () -> {
            logger.info("Running task");
            try {
                // Check if there is a next permutation
                if(iterator.hasNext()){
                    // Fetch data from API using the next permutation
                    List<MovieEntity> movies = getMoviesList(iterator.next());
                    // Add data to database
                    for(MovieEntity movie : movies){
                        //check if the movie already exists in the database
                        if(movieRepository.findByImdbID(movie.getImdbID()).isPresent()){
                            continue;
                        }
                        saveMovie(movie);
                        logger.info("Saved movie: " + movie.getTitle());
                    }
                } else {
                    // If there are no more permutations, stop the executor
                    executorService.shutdown();
                }
            } catch (Exception e) {
                logger.error("Error occurred while fetching movies", e);
                e.printStackTrace();
            }
        };

        // Schedule the task to run every 10 minutes
        executorService.scheduleWithFixedDelay(task, 0, 1, TimeUnit.MINUTES);
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

    public void fetchMovieData() {
        logger.info("Generating permutations");
        List<String> permutations = generatePermutations();
        logger.info("Starting to fetch movie data");
        startFetching(permutations);
    }

    public List<SimilarMovieEntity> searchMovie(String title){
        String url = API_URL + "&s=" + title;

        HttpGet request = httpGet(url);
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            //System.out.println(responseString);
            MovieAPIResponse movies = objectMapper.readValue(responseString, MovieAPIResponse.class);
            return movies.getSearch();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
        }
    }

    /**
     * Retrieves a movie by title.
     *
     * @param title The title of the movie.
     * @return The movie details.
     */
    public ResponseMovieDTO getMovie(String title){
        String url = API_URL + "&t=" + title;

        HttpGet request = httpGet(url);
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            var movie = objectMapper.readValue(responseString, ResponseMovieDTO.class);
            return movie;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
        }
    }

    /**
     * Retrieves similar movies based on title, genre, and type.
     *
     * @param title The title of the movie
     * @return A list of similar movies.
     */
     public List<SimilarMovieEntity> getSimilarMovies(String title){
        List<SimilarMovieEntity> allMovies = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = API_URL + "&s=" + title + "&page=" + page;

            HttpGet request = httpGet(url);
            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                // Check if the response contains an error message
                JsonNode responseJson = objectMapper.readTree(responseString);
                if (responseJson.has("Error")) {
                    logger.error("Error fetching movie with ID " + title + ": " + responseJson.get("Error").asText());
                    break;
                }
                MovieAPIResponse movies = objectMapper.readValue(responseString, MovieAPIResponse.class);

                allMovies.addAll(movies.getSearch());
                logger.info("API URL: " + url);
                logger.info("Fetched " + allMovies.size() + " movies");
                logger.info("Total results: " + movies.getTotalResults());
                // If the total results is more than the size of the search list, increment the page number and continue fetching
                if (Integer.parseInt(movies.getTotalResults()) > allMovies.size()) {
                    page++;
                } else {
                    break;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                request.releaseConnection();
            }
        }

        return allMovies;
    }

    public List<MovieEntity> getMoviesList(String title){
        List<SimilarMovieEntity> similarMovies = getSimilarMovies(title);
        List<MovieEntity> movies = new ArrayList<>();

        for(SimilarMovieEntity movie : similarMovies){
            String imdbID = movie.getImdbID();
            String url = API_URL + "&i=" + imdbID;

            HttpGet request = httpGet(url);
            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                var movieEntity = objectMapper.readValue(responseString, MovieEntity.class);
                movies.add(movieEntity);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                request.releaseConnection();
            }
        }

        return movies;
    }

    /**
     * Creates a new HttpGet request.
     *
     * @param url The URL for the request.
     * @return The HttpGet request.
     */
    private HttpGet httpGet(String url){
        return new HttpGet(url);
    }

    public void saveMovie(MovieEntity movieEntity){
        movieRepository.save(movieEntity);
    }

    public MovieEntity findByImdbID(String imdbID){
        return movieRepository.findByImdbID(imdbID).orElse(null);
    }


    public ResponseMovieDTO saveFavouriteMovie(String imdbID, String username){
        var user = userService.findByUsername(username);
        if(user == null){
            throw new UserService.UserNotFoundException(username);
        }

        MovieEntity savedMovie = findByImdbID(imdbID);
        if(user.getLstMovie() == null){
            user.setLstMovie(new ArrayList<>());
        }
        user.getLstMovie().add(savedMovie);
        userService.save(user);

        return modelMapper.map(savedMovie, ResponseMovieDTO.class);
    }

    /**
     * Retrieves all favourite movies for a user.
     *
     * @param username The username of the user.
     * @return A list of the user's favourite movies.
     */
    public List<ResponseMovieDTO> getFavouriteMovie(String username){
        var user = userService.findByUsername(username);
        if(user == null){
            throw new UserService.UserNotFoundException(username);
        }

        List<MovieEntity> lstMovies = user.getLstMovie();
        List<ResponseMovieDTO> responseMovieDTOList = new ArrayList<>();
        for(MovieEntity movie : lstMovies){
            responseMovieDTOList.add(modelMapper.map(movie, ResponseMovieDTO.class));
        }
        return responseMovieDTOList;
    }

    /**
     * Deletes a favourite movie for a user.
     *
     * @param username The username of the user.
     * @param imdbID   The IMDb ID of the movie.
     */
    public void deleteFavouriteMovie(String username, String imdbID){
        var user = userService.findByUsername(username);
        if(user == null){
            throw new UserService.UserNotFoundException(username);
        }

        List<MovieEntity> lstMovies = user.getLstMovie();
        for(MovieEntity movie : lstMovies){
            if(movie.getImdbID().equals(imdbID)){
                lstMovies.remove(movie);
                break;
            }
        }
        user.setLstMovie(lstMovies);
        userService.save(user);
    }

}
