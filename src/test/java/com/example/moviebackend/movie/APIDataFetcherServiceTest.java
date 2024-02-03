package com.example.moviebackend.movie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class APIDataFetcherServiceTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private APIDataFetcherService apiDataFetcherService;

    @Test
    void fetchData(){
        // Test the fetchData method
        when(movieService.getMoviesList(anyString())).thenReturn(List.of(new MovieEntity()));

        apiDataFetcherService.fetchData();

        // verify that getMoviesList is called
        verify(movieService, times(1)).getMoviesList(anyString());
    }

    @Test
    void generatePermutations(){
        // Test the generatePermutations method
        List<String> permutations = apiDataFetcherService.generatePermutations();
        assertEquals(17576, permutations.size());
    }
}