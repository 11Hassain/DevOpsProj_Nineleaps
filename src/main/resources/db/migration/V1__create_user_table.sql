-- V1__create_user_table.sql

-- Create the user table
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    enum_role VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP NOT NULL,
    last_logout TIMESTAMP NOT NULL,
    -- Add additional columns here, for example:
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_email UNIQUE (email) -- Ensure email uniqueness
);
