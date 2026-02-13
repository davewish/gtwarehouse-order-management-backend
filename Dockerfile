# Use a Maven image to build the project
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven files first to leverage caching
COPY pom.xml .
COPY src ./src

# Build the project (skip tests for faster build)
RUN mvn clean package -DskipTests

# Use lightweight Java runtime for running the app
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
