FROM maven:3.9.10-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

# Stage 2
FROM bellsoft/liberica-runtime-container:jdk-17-crac-cds-slim-stream-musl

WORKDIR /app

COPY --from=builder /app/target/bniqris-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT java -Xmx128m -jar app.jar