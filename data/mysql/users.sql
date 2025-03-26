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
    