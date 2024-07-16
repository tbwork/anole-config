package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AnoleConfigLocation
@SpringBootApplication
public class SpringExample {
    public static void main(String[] args) {
        AnoleApp.start();
        SpringApplication.run(SpringExample.class);
    }
}
