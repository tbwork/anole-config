package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.spring.annotation.Final;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
public class GreetingService {

    @Value("${anole.code.greeting}")
    private String greetings;

    @Final
    @Value("${anole.code.static.greeting}")
    private String staticGreetings;

    public String greet() {
        return this.greetings;
    }

    public String staticGreet() {
        return this.staticGreetings;
    }

}