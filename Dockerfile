FROM openjdk:11
COPY . /project
RUN cd /project
RUN cd /project ; ./mvnw clean package
RUN cp /project/target/*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]