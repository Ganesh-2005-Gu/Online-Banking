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

                String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100), " +
                        "role ENUM('USER', 'ADMIN') DEFAULT 'USER', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.executeUpdate(createUsers);

                String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                        "account_number VARCHAR(20) PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "balance DECIMAL(15, 2) DEFAULT 0.00, " +
                        "account_type ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                stmt.executeUpdate(createAccounts);

                String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "account_number VARCHAR(20) NOT NULL, " +
                        "type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL, " +
                        "amount DECIMAL(15, 2) NOT NULL, " +
                        "related_account VARCHAR(20), " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (account_number) REFERENCES accounts(account_number))";
                stmt.executeUpdate(createTransactions);

                String insertAdmin = "INSERT INTO users (username, password, full_name, role) " +
                        "SELECT 'admin', 'admin123', 'System Admin', 'ADMIN' " +
                        "WHERE NOT EXISTS (SELECT * FROM users WHERE username = 'admin')";
                stmt.executeUpdate(insertAdmin);

                System.out.println("Database tables checked/created successfully.");
            }

        } catch (Exception e) {
            System.err.println("DATABASE INITIALIZATION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
