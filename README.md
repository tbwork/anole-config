# Anole-Loader
# What it is
An awesome configuration loader for java. 
# what does it support
It supports all properties file which are required in runtime, such as spring properties files, log4j properties and so on.
# How to use it
1. Add this anole-loader to your build path by manual or maven dependency.
2. Create an enviroment file in your disk.
 * For windows: create a $env.anole file under `C://anole/`
 * For Linux: create a $env.anole file under `/etc/anole/`
 * For Max: create a $env.anole file under `/Users/anole/`
3. Create an anole config file in your classpath like
  ```
  #env:all
  key=value
  ```
  In the first line, `#env` means properties under this line will be loaded in and only in this enviroment, while `all` means it is suitable for all enviroments.
4. In your java codes, use them like:
```
 AnoleLocalConfig.get("key");
```
