FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/CheckServiceHealth-0.0.1-SNAPSHOT.jar /app/check-service-health.jar

ENTRYPOINT ["java", "-jar", "check-service-health.jar"]