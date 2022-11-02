FROM maven:latest as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn package

FROM openjdk:11
MAINTAINER intelliStart-klonn
COPY --from=build /usr/app/target/interview-planning-0.0.1-SNAPSHOT.jar /app/interview-planning.jar
ENTRYPOINT ["java", "-jar", "/app/interview-planning.jar"]