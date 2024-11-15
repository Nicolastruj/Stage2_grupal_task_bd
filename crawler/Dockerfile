# Use a base image with Java
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the entire project
COPY ../../ ./

# Build the entire project using Maven
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean install -DskipTests && \
    apt-get remove -y maven && \
    apt-get autoremove -y

# Copy only the built artifacts for this module
COPY target/crawler-1.0-SNAPSHOT.jar /app/crawler.jar

# Expose ports (if applicable)
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app/crawler.jar"]
