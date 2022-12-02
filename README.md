# intellistart-java-2022-klonn
The project provides RESTful Web Service for interview scheduling application.
It allows requesting web resources for the users being authorized with one of the following roles:

Roles:
* *Candidate* is interviewed. He/she also provides his availability slots
* *Interviewer* conducts an interview. He/she provides time slots in advance and sets the maximum number of bookings for the next week
* *Coordinator* searches for matching between interviewer and candidate slots and makes booking. He/she also grants and revokes user roles (interviewer or coordinator), can update interviewer slots, and see info on granted users and the dashboard for a week

# Install

Clone The GitHub Repository:
```
git clone git@github.com:julse-lia/intellistart-java-2022-klonn.git
./mvnw clean package
```
Create and set up `.env` file as shown in [env.example](./env.example)

## Running in Development mode

- Install [OpenJDK version: 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- Install [PostgreSQL version: 15.0 or higher](https://www.postgresql.org/download/)

Run app
```
java -jar ./target/interview-planning-*.jar
```

## Development and deployment With Docker

* Install [Docker](https://docs.docker.com/installation/#installation)
* Install [Compose](https://docs.docker.com/compose/install/)

Run application with docker-compose:
```
docker-compose up
```
It will run two containers:
* backend app built from `Dockerfile` at the root of project
* postgres db server with initial schema

Rebuild app:
```
docker-compose up --build
```
Stop the containers:
```
docker-compose down
```