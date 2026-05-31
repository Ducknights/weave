package org.example;

import org.junit.jupiter.api.Test;

class UserInfoControllerTest {

    int change(int x) {
        x = 20;
        return x;
    }

    @Test
    void contextLoads() {
        int a =10;
        a = change(a);
        System.out.println(a);
    }
}