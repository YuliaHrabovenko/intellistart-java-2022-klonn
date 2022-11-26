# intellistart-java-2022-klonn
The project provides backend part of interview scheduling application. 

Roles:
* *Candidate* can perform create/read/update operations over his/her slots
* *Interviewer* can perform create/read/update operations over his/her slots and set the maximum number of bookings for the next week
* *Coordinator* can perform CRUD operations over bookings, grant and revoke users(interviewers or coordinators), see info about granted users and dashboard for certain week

# Install

## Running for development with docker-compose
Run application with docker-compose:
```
docker-compose up
```
It will run two containers:
* spring boot backend app built from `Dockerfile` at the root of project
* postgres db server with initial schema

Rebuild app:
```
docker-compose up --build
```
Stop the containers:
```
docker-compose down
```
## Custom deployment / running

```
git clone git@github.com:julse-lia/intellistart-java-2022-klonn.git
./mvnw clean package
```

Set environment variables for DB connection and FB authentication in `.env` file
```
PG_USER=postgres
PG_PASSWORD=password
PG_DATABASE=interview_schedule

CLIENT_ID=
CLIENT_SECRET=
JWT_SECRET=secret
TOKEN_EXPIRATION_TIME=36000000
```

Run app
```
java -jar ./target/interview-planning-*.jar
```