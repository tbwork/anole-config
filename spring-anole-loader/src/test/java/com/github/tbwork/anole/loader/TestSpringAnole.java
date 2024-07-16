package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.spring.annotation.EnableAnoleAutoRefresh;
import com.github.tbwork.anole.spring.annotation.EnableSpringAnole;
import com.github.tbwork.anole.test.AnoleTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@AnoleTest
@EnableAnoleAutoRefresh
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
		Anole.setProperty("anole.code.greeting", "Hello, World!");
		message = greetingService.greet();
		Assert.assertTrue(message.equals("Hello, World!"));


		Anole.setProperty("anole.code.static.greeting", "Hello, World!");
		String staticMessage = greetingService.staticGreet();
		Assert.assertTrue(!staticMessage.equals("Hello, World!"));

	}
}
