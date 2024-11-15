# Fase de construcción con OpenJDK y Maven
FROM openjdk:21-jdk-slim AS build

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo POM principal
COPY ../pom.xml /app/pom.xml
# Copiar el proyecto completo al contenedor
COPY . .


# Construir solo el módulo 'query-engine'
RUN mvn -pl query-engine clean package -DskipTests

# Fase final: crear la imagen ligera usando solo el JAR empaquetado
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR empaquetado desde la fase de construcción
COPY --from=build /app/query-engine/target/query-engine-1.0-SNAPSHOT.jar query-engine.jar

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "query-engine.jar"]
