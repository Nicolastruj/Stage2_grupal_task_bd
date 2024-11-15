# Build phase
FROM openjdk:21-jdk-slim AS build

# Set working directory
WORKDIR /app

# Copy only the `query-engine` module
COPY ./ ./

# Install Maven, build the `query-engine` module, and clean up
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && apt-get remove -y maven && apt-get autoremove -y

# Final phase: create a lightweight image with the JAR
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build phase
COPY --from=build /app/target/query-engine-1.0-SNAPSHOT.jar query-engine.jar

# Command to run the application
CMD ["java", "-jar", "query-engine.jar"]
