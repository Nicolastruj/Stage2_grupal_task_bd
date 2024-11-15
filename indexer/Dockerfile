# Build phase with OpenJDK 21
FROM openjdk:21-jdk-slim AS build

# Set working directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml ./ 
COPY src ./src/

# Install Maven, build the project, and clean up
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && apt-get remove -y maven && apt-get autoremove -y

# Final phase: create a lightweight image with the JAR
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build phase
COPY --from=build /app/target/indexer-1.0-SNAPSHOT.jar indexer.jar

# Command to run the application
CMD ["java", "-jar", "indexer.jar"]
