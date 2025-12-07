# ---------- Build stage ----------
    FROM maven:3.9.6-eclipse-temurin-17 AS build
    WORKDIR /app
    
    # Copy Maven config and source
    COPY pom.xml .
    COPY src ./src
    
    # Build the jar (skip tests to speed up)
    RUN mvn clean package -DskipTests
    
    # ---------- Run stage ----------
    FROM eclipse-temurin:17-jre
    WORKDIR /app
    
    # Copy the built jar from the build stage
    COPY --from=build /app/target/*.jar app.jar
    
    # Spring Boot default port
    EXPOSE 8080
    
    # Run the application
    CMD ["java", "-jar", "app.jar"]
    