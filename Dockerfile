FROM openjdk:22.0.1-jdk-alpine
WORKDIR /app
COPY target/school-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
CMD [ "java","-jar","app.jar"]