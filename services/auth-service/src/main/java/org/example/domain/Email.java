package org.example.domain;

import lombok.Data;

@Data
public class Email {

    private String email;

    public static boolean verify(String email) {
        return email.matches("[\\w\\.\\-]+@[\\w\\.\\-]+[\\w]");
    }
}
