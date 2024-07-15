package com.github.tbwork.anole.loader;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
public class GreetingService {

    @Value("${anole.code.greeting}")
    private String greetings;

    public String greet() {
        return this.greetings;
    }

}