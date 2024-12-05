FROM openjdk:17-jdk-slim

COPY build/libs/Gil-Project-0.0.1-SNAPSHOT.jar /app/gil-project.jar

WORKDIR /app

CMD ["java", "-jar", "gil-project.jar"]

EXPOSE 8080
