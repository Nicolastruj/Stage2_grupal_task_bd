# Fase de construcción con OpenJDK 21
FROM openjdk:21-jdk-slim AS build

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo pom.xml y la carpeta src desde el directorio del módulo 'indexer'
COPY indexer/pom.xml ./
COPY indexer/src ./indexer/src

# Construir el proyecto (se descargan las dependencias y se empaqueta)
RUN mvn clean package -DskipTests  # Genera el JAR sin ejecutar las pruebas

# Fase final: crear la imagen ligera usando el JAR empaquetado
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR empaquetado desde la fase de construcción
COPY --from=build /app/target/indexer.jar indexer.jar

# Comando para ejecutar la aplicación
CMD ["java", "-cp", "indexer.jar", "software.guguel.apps.MainWithAggregatedStore"]

