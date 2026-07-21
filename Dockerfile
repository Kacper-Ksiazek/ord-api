# -------- STAGE 1: build --------
FROM maven:3.9-eclipse-temurin-24-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies first (layer cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the app (skip tests if needed)
RUN mvn clean package -DskipTests

# -------- STAGE 2: runtime --------
FROM eclipse-temurin:24.0.1_9-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
