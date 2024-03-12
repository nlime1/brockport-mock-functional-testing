# Pet Store Community

Pet store service application provides simple REST calls that can be used to exercise writing automated testing of API.

## Prerequisites

This project uses the following:
- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [IntelliJ community edition](https://www.jetbrains.com/idea/download/?section=windows)
- [Maven](https://maven.apache.org/) for projet management of dependcies and build
- [Postman](https://www.postman.com/) to perform REST API calls or use the IntelliJ plugin [Restful API Tool](https://plugins.jetbrains.com/plugin/22446-restful-api-tool)
- [Spring Boot](https://spring.io/guides/gs/spring-boot) to run the REST services.
- [Google Gson](https://github.com/google/gson) to handle JSON data

## Project structure

```
- petstorecommunity
  - src
    - main
	  - java
	  - resources
  - .getignore
  - pom.xml
  - README.md
```

## Getting started

- Clone the repository or download from [here](https://github.com/bkeenan26/petstorecommunity).
- From IntelliJ, import (File > New > Project from Existing Sources) by selecting the project folder.
- Run the spring application 
  * Press Shift+F10 or
  * use the play/run icon of the PetstoreserviceApplication.java file
- The *Console* tab shows the output Spring log messages. By default, the built-in Apache Tomcot server is listening on port 8080. Open your Web browser and go to http://localhost:8080/hello. If everything is setup correct, you should see your application response with ```Hello World!``` in the browser.