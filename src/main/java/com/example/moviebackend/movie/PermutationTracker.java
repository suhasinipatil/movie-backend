package com.example.moviebackend.movie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class PermutationTracker {
    static final String FILE_NAME = "last_permutation.txt";

    private static final Logger logger = LoggerFactory.getLogger(PermutationTracker.class);

    public static void writeLastPermutation(String permutation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(permutation);
        } catch (IOException e) {
            logger.error("Error writing last permutation to file");
            e.printStackTrace();
        }
    }

    public static String readLastPermutation() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            return reader.readLine();
        } catch (IOException e) {
            logger.error("Error reading last permutation from file");
            e.printStackTrace();
        }
        return null;
    }
}
