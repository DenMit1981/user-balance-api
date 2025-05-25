package com.denmit.userbalance;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Main {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String encoded1 = encoder.encode("12345678");
        String encoded2 = encoder.encode("super-password");

        System.out.println("12345678 -> " + encoded1);
        System.out.println("super-password -> " + encoded2);
    }
}

