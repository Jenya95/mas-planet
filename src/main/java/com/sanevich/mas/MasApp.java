package com.sanevich.mas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MasApp {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication.run(MasApp.class, args);
    }

}
