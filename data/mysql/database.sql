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