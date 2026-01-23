package org.example.domain;

import lombok.Data;

@Data
public class Password {
    private String password;

    public static Password encrypt(String password) {
        return new Password(password);
    }

}
