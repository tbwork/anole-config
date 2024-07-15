# Anole-Loader [![](https://jitpack.io/v/tbwork/anole-config.svg)](https://jitpack.io/#tbwork/anole-config)

# What it is

An awesome configuration loader for java. 

# Why we need it.
In enterprise java development, we use different kinds of third-party frameworks like Spring, Spring-Boot, Log4j, etc
. to develop applications rapidly. However, each framework has own configuration file, format, even the file-path and
 file-name, it would be annoying if you manage those files together. Anole-loader is a light framework to deal with
  this situation. By using it, you could manage all configurations together.

# What does it support

* Spring properties.
* Log4j properties.
* Logback properties.
* All frameworks accessing system properties to retrieve configurations. 


# How to use it

## 1 Add anole-loader to your build path manually or via maven dependency.
For maven, import it using:
```
<dependency>
  <groupId>com.github.tbwork</groupId>
  <artifactId>anole-loader</artifactId>
  <version>2.1.0</version>
</dependency>
```

## 2 Specify the runtime environment. There are 3 ways to specify the runtime enviroment.

> The default environment would be 'all'.

**Option 1**: Create an enviroment file in your disk.**
 * For windows: create a $env.env (e.g. test.env) file under `C://anole/`
 * For Linux: create a $env.env (e.g. dev.env) file under `/etc/anole/`
 * For Max: create a $env.env (e.g. production.env) file under `/Users/anole/`

**Option 2**: Specify a VM parameter named **"anole.runtime.currentEnvironment"** when you start java application, for example:
```
    java -DanoleEnv=test -jar XXX.jar
```
**Option 3**: Set the operating system environment variable named **"anole.runtime.currentEnvironment"**
 
## 3 Create an anole config file in your **classpath** like:

  ```
  #env:all
  
  ## define a string
  key=value
  ## define a number
  num = 123
  double = 123.1212
  float = 123.0
  
  ## define a boolean
  switch = true
  
  #env:test
  message=I'm\
  a test message
  
  ```
  Within the first line, `#env` means the following properties will be loaded in and only in this environment
  , while `all` means it is suitable for all environments.
  
### 3.1 Recursive variable reference

You can define a variable by referencing another variable using “${}”, see

```
name = tangbo
helloworld=hello, ${name}
```

> Once you update a property manually, all variables depend on this property would be refreshed automatically. 
  
## 4 Start your java program as an Anole application.

(1) Use the annotation of **@AnoleConfigLocation**
```
    @AnoleConfigLocation()
    public class Case1
    {  
        public static void main(String[] args) {
            AnoleApp.start();
        }
    }
```
The anole will load all \*.anole files in the classpath directory. 

Wanna custom the configuration files' locations? Do like this:
```
 @AnoleConfigLocation(locations = {"*.anole", "/*/jar/*op/*.txt"})
```
You can specify the ext-name of configuration file as you like.



(2) For Tomcat web application

> Obviously, this is out of date for now.

Configure the below lines in web.xml
```
      <context-param>
          <param-name>anoleConfigLocation</param-name>
          <param-value>
                dev.anole
                prd.anole
                config/common.custom_suffix
          </param-value>
      </context-param> 
      ...
      <listener>
            <listener-class>impl.loader.core.com.github.tbwork.anole.loader.WebAnoleLoaderListener</listener-class>
      </listener> 
```
**Tips**: Make sure to put the Anole listener to the top of all listener configurations so that the other frameworks can use the properties loaded by Anole. Those frameworks can be Spring, Log4j, Log4j2, Logback, etc.

## 5 In your java codes, use them like:

```
	Anole.getProperty ("key-name");
	Anole.getBoolProperty("key-name");
	Anole.getDoubleProperty("key-name");
	Anole.getFloatProperty("key-name");
	Anole.getIntProperty("key-name");
	Anole.getLongProperty("key-name");
	Anole.getShortProperty("key-name");
```

## 6 Spring Support

### 6.1 Code-based Configuration.

Starts manually like:

```java

@AnoleConfigLocation(locations={"*.anole"})
public class TestAnole {
	public static void main(String[] args) {
		AnoleApp.start(LogLevel.INFO);
	}
}
```
Or using the default settings:
```java
public class TestAnole {
    public static void main(String[] args) {
		AnoleApp.start();
	}
}
```

And then inject them into bean's fields like:

```java
public class GreetingService {

    @Value("${anole.code.greeting}")
    private String greetings;

    public String greet() {
        return this.greetings;
    }

}
```


### 6.2 XML-based Configuration

First, enable the Spring placeholder function by adding below codes to the spring configuration files(e.g. context.xml):
```
<bean id="enableplaceholder" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer" />  	
```
Then, Use **"${}"** to reference your variables like:
```
<bean id="test" class="${test.bean.name}" />
```


## 7 Other Frameworks Support

Anole supports most third-party frameworks, as long as these frameworks themselves read configurations from JVM runtime system properties. Therefore, in most cases, you can access Anole configurations using the common “${key}” syntax.

However, there are some exceptions, with slight differences, such as Log4j2. 

### 7.1 Log4j2 Support.

You can just use the "${sys:key}" syntax without any settings. Maybe someday, if the authors of Log4j2 ever stop being particular about the prefix, you might be able to remove the "sys:" prefix.

OR, you can import the anole-log4j2 dependency as :

```xml
<dependency>
    <groupId>com.github.tbwork</groupId>
    <artifactId>anole-log4j2</artifactId>
    <version>2.1.0</version> 
</dependency>
```
Then, you can access configurations managed by Anole using either ${anole:key} or ${a:key}.

Alternatively, you can define an empty <Properties></Properties> element in your `log4j2.xml`, which will activate Anole’s configuration interception plugin. 
In this case, you can use ${key} to access Anole configurations.

## 8 Integrate with JUnit5

Import the dependency like:
```xml
<dependency>
    <groupId>com.github.tbwork</groupId>
    <artifactId>anole-test</artifactId>
    <version>2.1.0</version> 
</dependency>

```

It is very easy to integrate Anole with JUnit5:

```
@AnoleTest
public class UserTest{ 
    @Test
    public void test(){
        // your test codes go here.
    } 
}

```

## 9 What is the principle

Anole-loader's principle is simple. It based on a fact that a vast majority of common-used third-party frameworks retrieve properties first from the JVM system properties and then from their own mechanisms. So, what anole-loader does is resolving property resources from different locations and registering them to JVM system property table. 
