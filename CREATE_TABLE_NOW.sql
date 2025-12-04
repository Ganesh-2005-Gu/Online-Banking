-- COPY AND PASTE THIS ENTIRE SCRIPT INTO YOUR DATABASE TOOL
-- (MySQL Workbench, phpMyAdmin, or any SQL client)
USE banking_system;
-- Create the missing otp_verification table
CREATE TABLE IF NOT EXISTS otp_verification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    purpose VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Update users table to remove TOTP from mfa_method enum
UPDATE users
SET mfa_method = 'NONE'
WHERE mfa_method = 'TOTP';
ALTER TABLE users
MODIFY COLUMN mfa_method ENUM('EMAIL', 'SMS', 'NONE') DEFAULT 'NONE';
-- Verify the table was created
SELECT 'SUCCESS: Table created!' AS Result;
DESCRIBE otp_verification;