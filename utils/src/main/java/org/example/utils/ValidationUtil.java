package org.example.utils;

public class ValidationUtil {
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_.+]*[\\w-_.]@(\\w+\\.)+\\w+\\w$";
        return email.matches(regex);
    }
}
