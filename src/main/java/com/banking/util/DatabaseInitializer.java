package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS banking_system");
                System.out.println("Database 'banking_system' checked/created.");
            }

            try (Connection conn = DriverManager.getConnection(DB_URL + "banking_system", USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {

                // 1. Users Table
                String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100), " +
                        "role ENUM('USER', 'ADMIN') DEFAULT 'USER', " +
                        "mfa_enabled BOOLEAN DEFAULT FALSE, " +
                        "mfa_secret VARCHAR(255), " +
                        "mfa_method ENUM('TOTP', 'EMAIL', 'NONE') DEFAULT 'NONE', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.executeUpdate(createUsers);

                // Apply updates to existing users table
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN phone_number VARCHAR(20)");
                } catch (Exception e) { /* Ignore if exists */ }
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE");
                } catch (Exception e) { /* Ignore if exists */ }
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN mfa_secret VARCHAR(255)");
                } catch (Exception e) { /* Ignore if exists */ }
                try {
                    stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN mfa_method ENUM('TOTP', 'EMAIL', 'SMS', 'NONE') DEFAULT 'NONE'");
                } catch (Exception e) { /* Ignore if exists */ }

                // 2. Accounts Table
                String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                        "account_number VARCHAR(20) PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "balance DECIMAL(15, 2) DEFAULT 0.00, " +
                        "account_type ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                stmt.executeUpdate(createAccounts);

                // 3. Transactions Table
                String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "account_number VARCHAR(20) NOT NULL, " +
                        "type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL, " +
                        "amount DECIMAL(15, 2) NOT NULL, " +
                        "related_account VARCHAR(20), " +
                        "status ENUM('PENDING', 'COMPLETED', 'FAILED', 'ROLLED_BACK') DEFAULT 'COMPLETED', " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (account_number) REFERENCES accounts(account_number))";
                stmt.executeUpdate(createTransactions);

                // Apply updates to existing transactions table
                try {
                    stmt.executeUpdate("ALTER TABLE transactions ADD COLUMN status ENUM('PENDING', 'COMPLETED', 'FAILED', 'ROLLED_BACK') DEFAULT 'COMPLETED'");
                } catch (Exception e) { /* Ignore if exists */ }

                // 4. Transaction Audit Log Table
                String createAuditLog = "CREATE TABLE IF NOT EXISTS transaction_audit_log (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "transaction_id INT, " +
                        "status VARCHAR(20), " +
                        "error_message TEXT, " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL)";
                stmt.executeUpdate(createAuditLog);

                // 5. OTP Verification Table
                String createOtp = "CREATE TABLE IF NOT EXISTS otp_verification (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "otp_code VARCHAR(10) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "expires_at TIMESTAMP NOT NULL, " +
                        "verified BOOLEAN DEFAULT FALSE, " +
                        "purpose VARCHAR(20) NOT NULL, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                stmt.executeUpdate(createOtp);

                String insertAdmin = "INSERT INTO users (username, password, full_name, role) " +
                        "SELECT 'admin', 'admin123', 'System Admin', 'ADMIN' " +
                        "WHERE NOT EXISTS (SELECT * FROM users WHERE username = 'admin')";
                stmt.executeUpdate(insertAdmin);

                System.out.println("Database tables checked/updated successfully.");
            }

        } catch (Exception e) {
            System.err.println("DATABASE INITIALIZATION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
