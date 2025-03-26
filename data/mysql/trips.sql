-- Trips Table (1 user can have many trips)
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

-- Tripmates Table (Stores emails)
-- SELECT "CREATING TRIPMATES TABLE...";
-- CREATE TABLE tripmates (
--     tripmate_id INT AUTO_INCREMENT,
--     trip_id INT NOT NULL, -- FK to trips table
--     email VARCHAR(100) NOT NULL,

--     CONSTRAINT pk_tripmate_id PRIMARY KEY(tripmate_id),
--     CONSTRAINT fk_trip_id FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE
-- );


