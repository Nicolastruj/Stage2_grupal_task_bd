# Build phase
FROM openjdk:21-jdk-slim AS build

# Set working directory
WORKDIR /app

# Copy the crawler module files
COPY ./ ./

# Install Maven, build the crawler module, and clean up
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && apt-get remove -y maven && apt-get autoremove -y

# Final phase: runtime image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build phase
COPY --from=build /app/target/crawler-1.0-SNAPSHOT.jar crawler.jar

# Pass the shared volume path as an environment variable
ENV SHARED_VOLUME_PATH="/shared-data"

# Command to run the application
CMD ["java", "-jar", "crawler.jar", "--shared-path", "/shared-data"]
