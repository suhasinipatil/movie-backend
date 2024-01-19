package com.example.moviebackend.movie;

import java.io.*;

public class PermutationTracker {
    private static final String FILE_NAME = "last_permutation.txt";

    public static void writeLastPermutation(String permutation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(permutation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readLastPermutation() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
