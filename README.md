# invoicing-api
DesignTechnologies Invoicing API Challenge implementation
### [Challenge requirements](https://github.com/clippings/documents-calculation-challenge)

## Prerequisites
The project requires `JDK 17` for development.  
You need to set the `JAVA_HOME` environment variable to the installation directory of your JDK in order to run the [`graddle wrapper`](./gradlew).  
To run the client side app you need `Node.js v16+`

## Running the project
You can run the project from the command line using the provided [`graddle wrapper`](./gradlew)  
Alternatively, you can import the project in your IDE of choice and run it from there.

### Starting the application
To start the Spring Boot application on port `8080` you can run the following command:
```
./gradlew clean bootRun
```
This will set up the required libraries as well.

If you want to run the application on a specific port you can pass an additional argument. For example:
```
./gradlew clean bootRun --args='--server.port=8888'
```
### Running unit tests
To run the unit tests use the following command:
```
./gradlew clean test
```

### Packaging
The following command will package the application into a runnable `jar`:
```
./gradlew clean bootJar
```
You can then run the application from the jar:
```
java -jar build/libs/invoice-0.0.1-SNAPSHOT.jar
```

### Documentation
You can generate java docs by running the following command:
```
./gradlew clean javaDoc
```
You can then browse the docs in a web browser by opening [build/docs/javadoc/index.html](build/docs/javadoc/index.html)

### Running the client app
First set up the dependencies
```
npm install
```
After that you can run the app in development mode on port `3000` by executing:
```
npm start
```
To pack a minified build execute:
```
npm run build
```