# Fase de construcción con OpenJDK y Maven
FROM openjdk:21-jdk-slim AS build

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app
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

# Construir el módulo 'indexer' usando el perfil Maven que se pasa como argumento
ARG PROFILE=expandedIndexer
RUN mvn -pl indexer clean package -DskipTests -P$PROFILE

# Fase final: crear la imagen ligera usando solo el JAR empaquetado
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR empaquetado desde la fase de construcción
COPY --from=build /app/indexer/target/indexer-1.0-SNAPSHOT.jar indexer.jar

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "indexer.jar"]

# Copiar el archivo POM principal
COPY ../pom.xml /app/pom.xml
# Copiar el proyecto completo al contenedor
COPY . .


# Construir solo el módulo 'indexer'
RUN mvn -pl indexer clean package -DskipTests

# Fase final: crear la imagen ligera usando solo el JAR empaquetado
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR empaquetado desde la fase de construcción
COPY --from=build /app/indexer/target/indexer-1.0-SNAPSHOT.jar indexer.jar

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "indexer.jar"]
