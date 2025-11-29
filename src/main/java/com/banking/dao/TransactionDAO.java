package com.banking.dao;

import com.banking.model.Transaction;
import com.banking.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean deposit(String accountNumber, BigDecimal amount) {
        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'DEPOSIT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                updateStmt.setBigDecimal(1, amount);
                updateStmt.setString(2, accountNumber);
                updateStmt.executeUpdate();

                logStmt.setString(1, accountNumber);
                logStmt.setBigDecimal(2, amount);
                logStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdraw(String accountNumber, BigDecimal amount) {

        String checkSql = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'WITHDRAWAL', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, accountNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    BigDecimal currentBalance = rs.getBigDecimal("balance");
                    if (currentBalance.compareTo(amount) < 0) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                updateStmt.setBigDecimal(1, amount);
                updateStmt.setString(2, accountNumber);
                updateStmt.executeUpdate();

                logStmt.setString(1, accountNumber);
                logStmt.setBigDecimal(2, amount);
                logStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transfer(String fromAccount, String toAccount, BigDecimal amount) {
        String checkSql = "SELECT balance FROM accounts WHERE account_number = ?";
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logFromSql = "INSERT INTO transactions (account_number, type, amount, related_account) VALUES (?, 'TRANSFER', ?, ?)";
        String logToSql = "INSERT INTO transactions (account_number, type, amount, related_account) VALUES (?, 'TRANSFER', ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, fromAccount);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    BigDecimal currentBalance = rs.getBigDecimal("balance");
                    if (currentBalance.compareTo(amount) < 0) {
                        return false; // Insufficient funds
                    }
                } else {
                    return false; // Account not found
                }
            }

            try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
                    PreparedStatement depositStmt = conn.prepareStatement(depositSql);
                    PreparedStatement logFromStmt = conn.prepareStatement(logFromSql);
                    PreparedStatement logToStmt = conn.prepareStatement(logToSql)) {

                withdrawStmt.setBigDecimal(1, amount);
                withdrawStmt.setString(2, fromAccount);
                withdrawStmt.executeUpdate();

                depositStmt.setBigDecimal(1, amount);
                depositStmt.setString(2, toAccount);
                depositStmt.executeUpdate();

                logFromStmt.setString(1, fromAccount);
                logFromStmt.setBigDecimal(2, amount.negate());

                logFromStmt.setString(3, toAccount);
                logFromStmt.executeUpdate();

                logToStmt.setString(1, toAccount);
                logToStmt.setBigDecimal(2, amount);
                logToStmt.setString(3, fromAccount);
                logToStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getString("type"),
                            rs.getBigDecimal("amount"),
                            rs.getString("related_account"),
                            rs.getTimestamp("timestamp")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
