FROM bellsoft/liberica-openjdk-alpine:21.0.3-cds

WORKDIR /app

COPY target/lafoca-backend-1.0.0.jar lafoca-backend.jar

EXPOSE 8080

CMD ["java", "-jar", "lafoca-backend.jar"]