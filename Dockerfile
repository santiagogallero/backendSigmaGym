# Usamos una imagen con Java 17
FROM openjdk:21-jdk-slim


# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado al contenedor
COPY backend-0.0.1-SNAPSHOT.jar app.jar


# Comando que ejecuta tu backend
ENTRYPOINT ["java", "-jar", "app.jar"]
