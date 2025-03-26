-- places Table (1 trip can have many places)
SELECT "CREATING PLACES TABLE...";
CREATE TABLE places (
    place_id INT AUTO_INCREMENT, -- PK (take from google place object)
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