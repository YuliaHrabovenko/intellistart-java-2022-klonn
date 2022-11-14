CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE candidate_time_slots (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	interview_date date NULL,
	start_time time NULL,
	end_time time NULL,
	name varchar(100) NULL,
	email varchar(64) NULL,
	CONSTRAINT candidate_time_slots_pkey PRIMARY KEY (id)
);

CREATE TABLE users (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	email varchar(64) NOT NULL,
	"role" varchar(30) NULL,
	name varchar(100) NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE interviewer_booking_limits (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	week_booking_limit int4 NOT NULL DEFAULT 0,
	current_booking_count int4 NULL DEFAULT 0,
	interviewer_id uuid NULL,
	week_number varchar(10) NOT NULL,
	CONSTRAINT interviewer_booking_limits_pkey PRIMARY KEY (id),
	CONSTRAINT interviewer_booking_limits_interviewer_id_fkey FOREIGN KEY (interviewer_id) REFERENCES users(id)
);

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

--add the first coordinator to the db on the app start
INSERT INTO users (name, email, role) VALUES ('Admin', 'first_coordinator@gmail.com', 'COORDINATOR');
