FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} cloudgateway.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/cloudgateway.jar"]

EXPOSE 9090