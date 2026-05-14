FROM gradle:8.7-jdk17 AS builder

WORKDIR /app
COPY . .
RUN gradle jar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/build/libs/calculator-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
