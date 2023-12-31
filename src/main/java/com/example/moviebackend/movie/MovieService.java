package com.example.moviebackend.movie;

import com.example.moviebackend.movie.dto.FavouriteMovieDTO;
import com.example.moviebackend.movie.dto.ResponseMovieDTO;
import com.example.moviebackend.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing movies.
 */
@Service
public class MovieService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "43dd2ba4";
    private static final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY;

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    private final HttpClient client = HttpClients.createDefault();
    private final UserService userService;


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
     * @param title The title of the movie.
     * @param genre The genre of the movie.
     * @param type  The type of the movie.
     * @return A list of similar movies.
     */
    public List<SimilarMovieEntity> getSimilarMovies(String title, String genre, String type){
        String url = API_URL + "&s=" + title + "&type=" + type + "&genre=" + genre;

        HttpGet request = httpGet(url);
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
            MovieAPIResponse movies = objectMapper.readValue(responseString, MovieAPIResponse.class);
            return movies.getSearch();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
        }
    }


    private HttpGet httpGet(String url){
        return new HttpGet(url);
    }


    /**
     * Saves a favourite movie for a user.
     *
     * @param favouriteMovieDTO The favourite movie details.
     * @return The saved favourite movie details.
     */
    public ResponseMovieDTO saveFavouriteMovie(FavouriteMovieDTO favouriteMovieDTO){
        var user = userService.findByUsername(favouriteMovieDTO.getUsername());
        if(user == null){
            throw new UserService.UserNotFoundException(favouriteMovieDTO.getUsername());
        }

        MovieEntity toBeSavedMovie = modelMapper.map(favouriteMovieDTO, MovieEntity.class);
        MovieEntity savedMovie = movieRepository.save(toBeSavedMovie);

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
     * @return The updated favourite movie details.
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
