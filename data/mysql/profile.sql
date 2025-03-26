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
	('e0f8b8c4', 2); -- need to change back user_id = 1 when deploying