# Use OpenJDK 17 slim as the base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml /app/
COPY src /app/src/

# Install Maven, build the project, and clean up
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && apt-get remove -y maven && apt-get autoremove -y

# Define volumes for shared data
VOLUME ["/data/datalake", "/data/metadata"]

# Set the entry point to run the application
CMD ["java", "-jar", "target/crawler-1.0-SNAPSHOT.jar"]
