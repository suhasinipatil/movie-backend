import com.example.moviebackend.movie.MovieEntity;
import com.example.moviebackend.movie.MovieRepository;
import com.example.moviebackend.movie.MovieService;
import com.example.moviebackend.movie.dto.FavouriteMovieDTO;
import com.example.moviebackend.movie.dto.ResponseMovieDTO;
import com.example.moviebackend.user.UserEntity;
import com.example.moviebackend.user.UserRepository;
import com.example.moviebackend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MovieService movieService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveFavouriteMovieShouldReturnMovieWhenUserExists() {
        FavouriteMovieDTO favouriteMovieDTO = new FavouriteMovieDTO();
        favouriteMovieDTO.setUsername("testUser");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle("testTitle");

        ResponseMovieDTO responseMovieDTO = new ResponseMovieDTO();
        responseMovieDTO.setTitle("testTitle");

        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(modelMapper.map(favouriteMovieDTO, MovieEntity.class)).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(modelMapper.map(movieEntity, ResponseMovieDTO.class)).thenReturn(responseMovieDTO);

        ResponseMovieDTO result = movieService.saveFavouriteMovie(favouriteMovieDTO);

        assertEquals(responseMovieDTO, result);
        verify(userService, times(2)).save(userEntity);
    }

    @Test
    public void saveFavouriteMovieShouldThrowExceptionWhenUserDoesNotExist() {
        FavouriteMovieDTO favouriteMovieDTO = new FavouriteMovieDTO();
        favouriteMovieDTO.setUsername("testUser");

        when(userService.findByUsername("testUser")).thenReturn(null);

        assertThrows(UserService.UserNotFoundException.class, () -> movieService.saveFavouriteMovie(favouriteMovieDTO));
    }
}