FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=/tmp/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
