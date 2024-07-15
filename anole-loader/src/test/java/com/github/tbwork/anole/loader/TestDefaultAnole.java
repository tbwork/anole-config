package com.github.tbwork.anole.loader;

import org.junit.Assert;

public class TestDefaultAnole {

    public static void main(String[] args) {
        AnoleApp.start();
        System.out.println(Anole.getProperty("b"));
    }
}
