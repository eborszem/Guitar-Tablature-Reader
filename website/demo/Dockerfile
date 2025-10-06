FROM eclipse-temurin:21-jdk

# set build arg for jar
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar

# copy compiled jar into container
COPY ${JAR_FILE} app.jar

# expose port
EXPOSE 8080

# command to run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]