# Anole-Loader
# What it is
An awesome congurations loader for java. 
# what does it support
It supports all properties file which are required in runtime, even those properties are used in the third-party frameworks.
# How to use it
1 Add this anole-loader to your build path by manual or maven dependency.
2 Create an enviroment file in your disk.
 * For windows: create a $env.anole file under `C://anole/`
 * For Linux: create a $env.anole file under `/etc/anole/`
 * For Max: create a $env.anole file under `/Users/anole/`
3 Create an anole config file in your **classpath** like
  ```
  #env:all
  key=value
  ```
  Within the first line, `#env` means properties under this line will be loaded in and only in this enviroment, while `all` means it is suitable for all enviroments.
4 Start your java instance as an Anole application.
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
When you use like below:
```
 @AnoleConfigLocation(locations="config.anole,config2.anyExtName")
```
You can specify the ext-name of configuration file as you like.

(2) For Tomcat web application
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
            <listener-class>org.tbwork.anole.loader.core.impl.WebAnoleLoaderListener</listener-class>
      </listener> 
```
**Tips**: Make sure to put the Anole listener to the top of all listener configurations so that the other frameworks can use the properties loaded by Anole. Those frameworks can be Spring, Log4j, Log4j2, Logback, etc.

5 In your java codes, use them like:
```
    Anole.getProperty ("key-name");
		Anole.getBoolProperty("key-name");
		Anole.getDoubleProperty("key-name");
		Anole.getFloatProperty("key-name");
		Anole.getIntProperty("key-name");
		Anole.getLongProperty("key-name");
		Anole.getShortProperty("key-name");
```

6 Spring support properties
First, enable the Spring placeholder function by adding below codes to the spring configuration files(e.g. context.xml):
```
<bean id="enableplaceholder" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer" />  	
```
Then, Use **"${}"** to reference your variables like:
```
<bean id="test" class="${test.bean.name}" />
```

7 Support other frameworks
Anole support all third-party frameworks via injecting the properties to the Java runtime system properties.
So to support those frameworks, you just neet to use the method like referencing a system properties.
For example, to Log4j, it would be like :
```
${key}
```
To Log4j2, it would be like:
```
${sys:key}
```



