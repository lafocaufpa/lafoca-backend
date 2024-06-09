FROM bellsoft/liberica-openjdk-alpine:21.0.3-cds

WORKDIR /app

ARG JAR_FILE

COPY target/${JAR_FILE} /app/lafoca-backend.jar
COPY wait-for-it.sh /wait-for-it.sh

RUN chmod +x /wait-for-it.sh

EXPOSE 8080

CMD ["java", "-jar", "lafoca-backend.jar"]