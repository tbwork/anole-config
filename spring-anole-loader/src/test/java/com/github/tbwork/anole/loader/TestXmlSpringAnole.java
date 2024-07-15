package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.test.AnoleTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@AnoleTest
public class TestXmlSpringAnole {

    @Test
    public void test(){
        // 加载 Spring 配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 获取 Bean 实例
        GreetingService myBean = (GreetingService) context.getBean("greeting");

        // 使用 Bean
        Assert.assertTrue("Hello, Spring!".equals( myBean.greet()));
    }

}
