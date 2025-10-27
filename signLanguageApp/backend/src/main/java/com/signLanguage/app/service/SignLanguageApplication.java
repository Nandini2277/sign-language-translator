package com.signlanguage.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SignLanguageApplication {
    
    static {
        // Load OpenCV native library
        System.loadLibrary("opencv_java4120");
    }
    
    public static void main(String[] args) {
        SpringApplication.run(SignLanguageApplication.class, args);
    }
}