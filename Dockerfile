# --- Etapa 1: Construir la aplicación ---
# Usamos una imagen que ya tiene Gradle y Java 17 (JDK)
FROM gradle:8.5-jdk17 AS builder

# Creamos un directorio de trabajo
WORKDIR /home/gradle/src

# Copiamos tódo el código de nuestro proyecto al contenedor
COPY --chown=gradle:gradle . .

# Ejecutamos el comando de Gradle para crear el archivo .jar "gordo" (con todas las dependencias)
RUN gradle shadowJar --no-daemon


# --- Etapa 2: Ejecutar la aplicación ---
# Usamos una imagen mucho más pequeña que solo tiene lo necesario para ejecutar Java (JRE)
FROM openjdk:17-jre-slim

# Creamos el directorio de la aplicación
WORKDIR /app

# Copiamos solo el archivo .jar que se creó en la etapa anterior
COPY --from=builder /home/gradle/src/build/libs/*-all.jar ./app.jar

# Exponemos el puerto 8080 para que Render pueda comunicarse con nuestra app
EXPOSE 8080

# El comando final para arrancar el servidor Ktor
CMD ["java", "-jar", "app.jar"]