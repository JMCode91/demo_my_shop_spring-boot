# Etapa 1: Usamos una máquina con Maven y Java 21 para fabricar el .jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Usamos una máquina más ligera solo con Java 21 para ejecutar la tienda
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copiamos el .jar que se acaba de crear y lo renombramos a "app.jar" para que sea más fácil
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Dejamos la puerta preparada para recibir los comandos
ENTRYPOINT ["java", "-jar", "app.jar"]