FROM openjdk:17-jdk-alpine

WORKDIR /app
RUN mkdir -p logs

COPY gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]
