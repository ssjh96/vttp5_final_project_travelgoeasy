-- DATABASE
-- drop the database if exists
DROP DATABASE IF EXISTS travelgoeasy;

-- create the database
-- create database if not exists travelgoeasy; -- already have drop database if exists
CREATE DATABASE travelgoeasy;

-- select the database
USE travelgoeasy;

-- Grant fred access to the DB
SELECT "GRANTING ALL PRIVILEGES TO FRED..";
GRANT ALL PRIVILEGES ON travelgoeasy.* TO 'fred'@'%';

-- Apply changes to privileges
FLUSH PRIVILEGES;



-- USERS TABLE
SELECT "CREATING USERS TABLE...";
CREATE TABLE users (
    id INT AUTO_INCREMENT, -- this is the PK
    username VARCHAR(50) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_pro BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_id primary key(id)
);

-- Inserting ADMIN
SELECT "INSERTING ADMIN INTO USERS TABLE";
INSERT INTO users 
	(username, password, email, role, is_pro)
VALUES 
	('shamus', '$2a$10$7tSznXKJ.dNPQCfuJCwyDeOqrw.lU3AKBCe0Ye8gAfafbOuVGL/BO', 'shamus@travelgoeasy.com', 'ADMIN', TRUE);


-- PROFILE TABLE
-- 1-1 r/s
SELECT "CREATING PROFILE TABLE...";
CREATE TABLE profile (
    profile_id VARCHAR(8) NOT NULL, -- PK (UUID)
    user_id INT UNIQUE NOT NULL, -- FK
    first_name VARCHAR(255) DEFAULT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    -- profile_picture mediumblob DEFAULT NULL,
    profile_picture VARCHAR(255) DEFAULT NULL,

    CONSTRAINT pk_profile_id PRIMARY KEY(profile_id),
    CONSTRAINT fk_profile_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE -- prevents orphan
);    

-- Inserting ADMIN profile
SELECT "INSERTING ADMIN PROFILE INTO PROFILES TABLE";
INSERT INTO profile 
	(profile_id, user_id)
VALUES 
	('e0f8b8c4', 1); 



-- TRIPS TABLE
-- 1-many r/s, 1 user can have many trips
SELECT "CREATING TRIPS TABLE...";
CREATE TABLE trips (
    trip_id INT AUTO_INCREMENT, -- PK
    user_id INT NOT NULL, -- FK
    trip_name VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL, 
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    trip_mates TEXT DEFAULT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_trip PRIMARY KEY(trip_id),
    CONSTRAINT fk_p_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE -- prevents orphan
);



-- PLACES TABLE
-- 1-many r/s, 1 trip can have many places
SELECT "CREATING PLACES TABLE...";
CREATE TABLE places (
    place_id INT AUTO_INCREMENT, -- PK
    trip_id INT NOT NULL, -- FK
    place_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    day_number INT NOT NULL,
    order_index INT NOT NULL, -- Determines the order in the itinerary
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_location PRIMARY KEY(place_id),
    CONSTRAINT fk_trip_id FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE -- prevents orphan
);

    