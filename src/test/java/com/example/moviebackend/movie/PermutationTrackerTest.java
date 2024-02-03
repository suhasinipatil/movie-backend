package com.example.moviebackend.movie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermutationTrackerTest {

    @InjectMocks
    PermutationTracker permutationTracker;

    @Test
    void writeLastPermutation(){
        // Given
        String permutation = "1234";

        // When
        PermutationTracker.writeLastPermutation(permutation);

        // Then
        assertEquals(permutation, PermutationTracker.readLastPermutation());
    }

    @Test
    void readLastPermutation(){
        // Given
        String permutation = "1234";
        PermutationTracker.writeLastPermutation(permutation);

        // When
        String lastPermutation = PermutationTracker.readLastPermutation();

        // Then
        assertEquals(permutation, lastPermutation);
    }
}