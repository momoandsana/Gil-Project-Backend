FROM openjdk:17-jdk
WORKDIR /app
COPY target/gil-project.jar ./gil-project.jar
EXPOSE 9000
CMD ["java", "-jar", "gil-project.jar"]
