# Build stage with Java 22
FROM maven:3.9.6-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage with Java 22
FROM eclipse-temurin:22-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/school-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
