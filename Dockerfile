FROM openjdk:11
MAINTAINER intelliStart-klonn
EXPOSE 8080
COPY target/interview-planning-0.0.1-SNAPSHOT.jar /interview-planning.jar
ENTRYPOINT ["java", "-jar", "/interview-planning.jar"]