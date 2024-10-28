# Use an official Maven image as the build environment
FROM maven:3.9.9-eclipse-temurin-21 as build

# Set the working directory in the container
WORKDIR /app

# Copy the whole project to the container
COPY . .

# Run Maven to build only the `query-engine` module
RUN mvn -pl query-engine clean package -DskipTests

# Use an official OpenJDK image as the base runtime image
FROM eclipse-temurin:21-jre

# Set the working directory for the final stage
WORKDIR /app

# Copy the JAR file from the build stage to the runtime stage
COPY --from=build /app/query-engine/target/query-engine-1.0-SNAPSHOT.jar /app/query-engine.jar

# Specify the command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/query-engine.jar"]