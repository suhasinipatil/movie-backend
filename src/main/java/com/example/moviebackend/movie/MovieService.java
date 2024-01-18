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
import java.util.Comparator;
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

    public List<MovieEntity> getAllMovies(String keyword){
        return movieRepository.findByKeyword(keyword);
    }

    //write method to give movie recommendation
    public List<MovieEntity> getRecommendedMovies(String imdbID){
        MovieEntity movieEntity = movieRepository.findByImdbID(imdbID).orElse(null);
        if(movieEntity == null){
            throw new MovieNotFoundException(imdbID);
        }

        List<MovieEntity> recommendedMovies = new ArrayList<>();
        List<MovieEntity> allMovies = movieRepository.findAll();
        String genre = movieEntity.getGenre();
        String imdbRatingStr = movieEntity.getImdbRating();
        float imdbRating = "N/A".equals(imdbRatingStr) ? -1.0f : Float.parseFloat(imdbRatingStr);
        String director = movieEntity.getDirector();
        String writer = movieEntity.getWriter();
        String language = movieEntity.getLanguage();
        String actors = movieEntity.getActors();

        float lowerBound = imdbRating - 2.0f; // 2 less than the current movie's rating
        float upperBound = imdbRating + 2.0f; // 2 more than the current movie's rating

       /* for(MovieEntity movie : allMovies){
            String movieImdbRatingStr = movie.getImdbRating();
            float movieImdbRating = "N/A".equals(movieImdbRatingStr) ? -1.0f : Float.parseFloat(movieImdbRatingStr);
            if((!"N/A".equals(genre) && movie.getGenre().contains(genre))
                    && (!"N/A".equals(director) && movie.getDirector().contains(director))
                    && (!"N/A".equals(writer) && movie.getWriter().contains(writer))
                    && (!"N/A".equals(language) && movie.getLanguage().contains(language))
                    && (!"N/A".equals(actors) && movie.getActors().contains(actors))
                    && (imdbRating != -1.0f && movieImdbRating >= lowerBound && movieImdbRating <= upperBound)){
                recommendedMovies.add(movie);
            }
        }*/

        for(MovieEntity movie : allMovies){
            // Skip the given movie
            if(movie.getImdbID().equals(imdbID)){
                continue;
            }

            String movieImdbRatingStr = movie.getImdbRating();
            float movieImdbRating = "N/A".equals(movieImdbRatingStr) ? -1.0f : Float.parseFloat(movieImdbRatingStr);
            if((!"N/A".equals(genre) && movie.getGenre().contains(genre))
                    || (!"N/A".equals(director) && movie.getDirector().contains(director))
                    || (!"N/A".equals(writer) && movie.getWriter().contains(writer))
                    || (!"N/A".equals(language) && movie.getLanguage().contains(language))
                    || (!"N/A".equals(actors) && movie.getActors().contains(actors))
                    || (imdbRating != -1.0f && movieImdbRating >= lowerBound && movieImdbRating <= upperBound)){
                recommendedMovies.add(movie);
            }
        }

        recommendedMovies.sort(Comparator.comparing(MovieEntity::getYear).reversed());
        recommendedMovies.removeIf(movie -> movie.getPoster() == null || "N/A".equals(movie.getPoster()));

        return recommendedMovies;
    }

    // List movies of the latest year from now
    public List<MovieEntity> filterMoviesByYear(){
        int currentYear = java.time.Year.now().getValue() - 2;
        List<MovieEntity> movieEntities = movieRepository.findByYear(currentYear + "");

        // Remove movies which don't have a poster
        movieEntities.removeIf(movie -> movie.getPoster() == null || "N/A".equals(movie.getPoster()));

        // Sort movies first which have imdb and in highest first order
        // Movies with imdb as N/A are put at the end
        movieEntities.sort((movie1, movie2) -> {
            String rating1 = movie1.getImdbRating();
            String rating2 = movie2.getImdbRating();

            if("N/A".equals(rating1) && "N/A".equals(rating2)){
                return 0;
            } else if("N/A".equals(rating1)){
                return 1;
            } else if("N/A".equals(rating2)){
                return -1;
            } else {
                return Float.compare(Float.parseFloat(rating2), Float.parseFloat(rating1));
            }
        });

        return movieEntities;
    }

    /**
     * Retrieves similar movies based on title, genre, and type.
     *
     * @param title The title of the movie
     * @return A list of similar movies.
     */
    public List<SimilarMovieEntity> searchMovies(String title){
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

    public String convertRuntime(String runtime){
        // Remove non-digit characters from the runtime string
        String digits = runtime.replaceAll("\\D+", "");
        int minutes = Integer.parseInt(digits);

        if(minutes < 60){
            return minutes + "min";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return hours + "h " + remainingMinutes + "min";
        }
    }

    public List<MovieEntity> getMoviesList(String title){
        List<SimilarMovieEntity> similarMovies = searchMovies(title);
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
                //convert move runtime into h and minutes
                if(movieEntity.getRuntime().contains("N/A")){
                    movies.add(movieEntity);
                    continue;
                }
                String convertedRuntime = convertRuntime(movieEntity.getRuntime());
                movieEntity.setRuntime(convertedRuntime);
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
        MovieEntity movieEntity = movieRepository.findByImdbID(imdbID).orElse(null);
        if(movieEntity == null){
            throw new MovieNotFoundException(imdbID);
        }
        return movieEntity;
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

    //Movie not found Exception
    public static class MovieNotFoundException extends RuntimeException {
        public MovieNotFoundException(String imdbID) {
            super("Could not find movie with ID " + imdbID);
        }
    }

}
