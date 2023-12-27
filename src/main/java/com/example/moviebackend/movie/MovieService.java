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

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "43dd2ba4";
    private static final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY;

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    private final HttpClient client = HttpClients.createDefault();
    private final UserService userService;

    public MovieService(ModelMapper modelMapper, MovieRepository movieRepository, UserService userService){
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.userService = userService;
    }

    public ResponseMovieDTO getMovie(String title){
        String url = API_URL + "&t=" + title;

        HttpGet request = httpGet(url);
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return objectMapper.readValue(responseString, ResponseMovieDTO.class, );

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
        }
    }

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

    public FavouriteMovieDTO saveFavouriteMovie(FavouriteMovieDTO favouriteMovieDTO){
        var user = userService.findByUsername(favouriteMovieDTO.getUsername());
        if(user == null){
            throw new UserService.UserNotFoundException(favouriteMovieDTO.getUsername());
        }

        MovieEntity toBeSavedMovie = modelMapper.map(favouriteMovieDTO, MovieEntity.class);
        MovieEntity savedMovie = movieRepository.save(toBeSavedMovie);

        user.getLstMovie().add(savedMovie);
        userService.save(user);

        return modelMapper.map(savedMovie, FavouriteMovieDTO.class);
    }


    public List<FavouriteMovieDTO> getFavouriteMovie(String username){
        var user = userService.findByUsername(username);
        if(user == null){
            throw new UserService.UserNotFoundException(username);
        }

        List<MovieEntity> lstMovies = user.getLstMovie();
        List<FavouriteMovieDTO> favouriteMovieDTOList = new ArrayList<>();
        for(MovieEntity movie : lstMovies){
            favouriteMovieDTOList.add(modelMapper.map(movie, FavouriteMovieDTO.class));
        }
        return favouriteMovieDTOList;
    }

}
