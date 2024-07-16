package com.github.tbwork.anole.test;

import com.github.tbwork.anole.loader.Anole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@AnoleTest
public class AnoleBasicTests {


    @Test
    public void testReadValues(){

        System.out.println(Anole.getProperty("a"));
        Assertions.assertTrue(Anole.getProperty("a").equals("true"));
        Assertions.assertTrue(Anole.getBoolProperty("a"));

        System.out.println(Anole.getProperty("b"));
        Assertions.assertTrue(Anole.getProperty("b").equals("hello"));

        System.out.println(Anole.getProperty("c"));
        Assertions.assertTrue(Anole.getProperty("c").equals("hello, anole"));

        Anole.setProperty("a","false");
        System.out.println(Anole.getProperty("c"));
        Assertions.assertTrue(Anole.getProperty("c").equals("hey, anole"));

    }
}
