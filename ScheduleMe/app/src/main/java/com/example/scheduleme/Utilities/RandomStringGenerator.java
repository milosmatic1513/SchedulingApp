package com.example.scheduleme.Utilities;

import java.util.Random;

public class RandomStringGenerator {

    static String alphabet = "abcdefghijklmnopqrstuvwxyz";
    static String alphabetCapital = alphabet.toUpperCase();
    static String numbers="0123456789";
    static String data = alphabet+alphabetCapital+numbers;

    public static String generateString(int length){
        Random rnd = new Random();
        StringBuilder string = new StringBuilder();
        while (string.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * data.length());
            string.append(data.charAt(index));
        }
        return string.toString();
    }
}
