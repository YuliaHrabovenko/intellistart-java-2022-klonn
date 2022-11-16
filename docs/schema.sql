CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE candidate_time_slots (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	interview_date date NULL,
	start_time time NULL,
	end_time time NULL,
	email varchar(255) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT candidate_time_slots_pkey PRIMARY KEY (id)
);

CREATE INDEX candidate_time_slots_email_idx
ON candidate_time_slots (email);

CREATE INDEX candidate_time_slots_interview_date_idx
ON candidate_time_slots (interview_date);

CREATE TABLE users (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	email varchar(64) NOT NULL,
	"role" varchar(30) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE INDEX users_role_idx
ON users (role);

CREATE TABLE interviewer_booking_limits (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	week_booking_limit int4 NOT NULL DEFAULT 0,
	current_booking_count int4 NULL DEFAULT 0,
	interviewer_id uuid NULL,
	week_number varchar(10) NOT NULL,
	CONSTRAINT interviewer_booking_limits_pkey PRIMARY KEY (id),
	CONSTRAINT interviewer_booking_limits_interviewer_id_fkey FOREIGN KEY (interviewer_id) REFERENCES users(id)
);

CREATE INDEX interviewer_booking_limits_week_number_idx
ON interviewer_booking_limits (week_number);

CREATE TABLE interviewer_time_slots (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	day_of_week int4 NOT NULL,
	interviewer_id uuid NULL,
	start_time time NULL,
	end_time time NULL,
	week_number varchar(10) NOT NULL,
	CONSTRAINT interviewer_time_slots_pkey PRIMARY KEY (id),
	CONSTRAINT interviewer_time_slots_interviewer_id_fkey FOREIGN KEY (interviewer_id) REFERENCES users(id)
);

CREATE INDEX interviewer_time_slots_week_number_idx
ON interviewer_time_slots (week_number);

CREATE TABLE bookings (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	subject varchar(255) NOT NULL,
	description varchar(4000) NOT NULL,
	candidate_time_slot_id uuid NULL,
	interviewer_time_slot_id uuid NULL,
	start_time time NULL,
	end_time time NULL,
	CONSTRAINT bookings_pkey PRIMARY KEY (id),
	CONSTRAINT bookings_candidate_time_slot_id_fkey FOREIGN KEY (candidate_time_slot_id) REFERENCES candidate_time_slots(id),
	CONSTRAINT bookings_interviewer_time_slot_id_fkey FOREIGN KEY (interviewer_time_slot_id) REFERENCES interviewer_time_slots(id)
);

CREATE INDEX bookings_candidate_time_slot_id_idx
ON bookings (candidate_time_slot_id);

CREATE INDEX bookings_interviewer_time_slot_id_idx
ON bookings (interviewer_time_slot_id);

--add the first coordinator to the db on the app start
INSERT INTO users (name, email, role) VALUES ('first_coordinator@gmail.com', 'COORDINATOR');
