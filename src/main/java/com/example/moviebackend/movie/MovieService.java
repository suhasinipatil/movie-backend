package com.example.moviebackend.movie;

import com.example.moviebackend.movie.dto.FavouriteMovieDTO;
import com.example.moviebackend.user.UserEntity;
import com.example.moviebackend.user.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "43dd2ba4";
    private static final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY;

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    private final UserRepository userRepository;
    private final HttpClient client = HttpClients.createDefault();

    public MovieService(ModelMapper modelMapper, MovieRepository movieRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public MovieEntity getMovie(String title) {
        String url = API_URL + "&t=" + title;

        HttpGet request = httpGet(url);
        try{
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            ///System.out.println(responseString);
            return objectMapper.readValue(responseString, MovieEntity.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
        }
    }

    public List<SimilarMovieEntity> getSimilarMovies(String title, String genre, String type) {
        String url = API_URL + "&s=" + title + "&type=" + type + "&genre=" + genre;

        HttpGet request = httpGet(url);
        try{
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

    //public Fa
    private HttpGet httpGet(String url) {
        return new HttpGet(url);
    }

    public FavouriteMovieDTO saveFavoriteMovie(FavouriteMovieDTO favouriteMovieDTO){
        MovieEntity toBeSavedMovie = modelMapper.map(favouriteMovieDTO, MovieEntity.class);
        MovieEntity savedMovie = movieRepository.save(toBeSavedMovie);
        return modelMapper.map(savedMovie, FavouriteMovieDTO.class);
    }

    public List<FavouriteMovieDTO> getAllFavoriteMovies(String username){
        var user = userRepository.findByUsername(username);
        if(user == null){
            throw new UserService.UserNotFoundException(username);
        }
        var lstMovies = movieRepository.findAllByUsername(user.getId());
        if(lstMovies.isPresent()){
            List<FavouriteMovieDTO> favouriteMovieDTOList = new ArrayList<>();
            for (MovieEntity movie: lstMovies.get())
            {
                favouriteMovieDTOList.add(modelMapper.map(movie, FavouriteMovieDTO.class));
            }
            return favouriteMovieDTOList;
        }
        throw new NoFavoriteMovieException(username);
    }

    public static class NoFavoriteMovieException extends IllegalArgumentException{
        public NoFavoriteMovieException(String username) {
            super("No Favorite movie for user " + username);
        }
    }
}
