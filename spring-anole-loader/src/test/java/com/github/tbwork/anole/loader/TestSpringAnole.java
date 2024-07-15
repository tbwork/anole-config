package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.test.AnoleTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@AnoleTest
@SpringBootTest
@AnoleConfigLocation(locations = {"*.anole"})
@ContextConfiguration(classes = {GreetingService.class})
public class TestSpringAnole {

	@Autowired
	private GreetingService greetingService;

	@Test
	public void test() {
		String message = greetingService.greet();
		Assert.assertTrue(message.equals("Hello, Spring!"));
	}
}
