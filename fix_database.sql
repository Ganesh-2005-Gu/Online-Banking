-- Complete fix for OTP verification
USE banking_system;
-- Step 1: Create the missing otp_verification table
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
-- Step 2: Update users table to remove TOTP from mfa_method enum
-- First, update any existing TOTP users to NONE
UPDATE users
SET mfa_method = 'NONE'
WHERE mfa_method = 'TOTP';
-- Modify the column to only allow EMAIL and NONE
ALTER TABLE users
MODIFY COLUMN mfa_method ENUM('EMAIL', 'SMS', 'NONE') DEFAULT 'NONE';
-- Step 3: Verify the changes
SELECT 'Tables created/updated successfully!' AS Status;
DESCRIBE otp_verification;
SHOW COLUMNS
FROM users LIKE 'mfa_method';