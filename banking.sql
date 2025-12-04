CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;
-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    -- In a real app, this should be hashed
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    mfa_method ENUM('TOTP', 'EMAIL', 'NONE') DEFAULT 'NONE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    account_type ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    related_account VARCHAR(20),
    -- For transfers
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'ROLLED_BACK') DEFAULT 'COMPLETED',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
-- Transaction Audit Log Table
CREATE TABLE IF NOT EXISTS transaction_audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT,
    status VARCHAR(20),
    error_message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE
    SET NULL
);
-- OTP Verification Table
CREATE TABLE IF NOT EXISTS otp_verification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    purpose VARCHAR(20) NOT NULL,
    -- LOGIN, TRANSACTION
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Insert Default Admin (password: admin123)
INSERT INTO users (username, password, full_name, role)
SELECT 'admin',
    'admin123',
    'System Admin',
    'ADMIN'
WHERE NOT EXISTS (
        SELECT *
        FROM users
        WHERE username = 'admin'
    );