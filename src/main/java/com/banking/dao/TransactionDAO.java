package com.banking.dao;

import com.banking.model.Transaction;
import com.banking.model.TransactionResult;
import com.banking.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public TransactionResult deposit(String accountNumber, BigDecimal amount) {
        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount, status) VALUES (?, 'DEPOSIT', ?, 'COMPLETED')";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql, Statement.RETURN_GENERATED_KEYS)) {

                updateStmt.setBigDecimal(1, amount);
                updateStmt.setString(2, accountNumber);
                int rows = updateStmt.executeUpdate();

                if (rows == 0) {
                    conn.rollback();
                    return new TransactionResult(false, "Account not found: " + accountNumber);
                }

                logStmt.setString(1, accountNumber);
                logStmt.setBigDecimal(2, amount);
                logStmt.executeUpdate();
                
                int transactionId = -1;
                try (ResultSet generatedKeys = logStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transactionId = generatedKeys.getInt(1);
                    }
                }

                logAudit(conn, transactionId, "COMPLETED", null);
                
                conn.commit();
                return new TransactionResult(true, transactionId, "Deposit successful");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return new TransactionResult(false, "Database error: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TransactionResult(false, "Connection error: " + e.getMessage());
        }
    }

    public TransactionResult withdraw(String accountNumber, BigDecimal amount) {
        String checkSql = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (account_number, type, amount, status) VALUES (?, 'WITHDRAWAL', ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Use serializable isolation to prevent race conditions on balance check
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, accountNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    BigDecimal currentBalance = rs.getBigDecimal("balance");
                    if (currentBalance.compareTo(amount) < 0) {
                        conn.rollback();
                        return new TransactionResult(false, "Insufficient funds");
                    }
                } else {
                    conn.rollback();
                    return new TransactionResult(false, "Account not found");
                }
            }

            int transactionId = -1;
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql, Statement.RETURN_GENERATED_KEYS)) {

                updateStmt.setBigDecimal(1, amount);
                updateStmt.setString(2, accountNumber);
                updateStmt.executeUpdate();

                logStmt.setString(1, accountNumber);
                logStmt.setBigDecimal(2, amount);
                logStmt.setString(3, "COMPLETED");
                logStmt.executeUpdate();
                
                try (ResultSet generatedKeys = logStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transactionId = generatedKeys.getInt(1);
                    }
                }
                
                logAudit(conn, transactionId, "COMPLETED", null);

                conn.commit();
                return new TransactionResult(true, transactionId, "Withdrawal successful");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return new TransactionResult(false, "Database error: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TransactionResult(false, "Connection error: " + e.getMessage());
        }
    }

    public TransactionResult transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            return new TransactionResult(false, "Cannot transfer to the same account");
        }

        String checkSql = "SELECT balance FROM accounts WHERE account_number = ?";
        String checkDestSql = "SELECT count(*) FROM accounts WHERE account_number = ?";
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logFromSql = "INSERT INTO transactions (account_number, type, amount, related_account, status) VALUES (?, 'TRANSFER', ?, ?, ?)";
        String logToSql = "INSERT INTO transactions (account_number, type, amount, related_account, status) VALUES (?, 'TRANSFER', ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Critical for transfers to ensure no other transaction modifies these accounts
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // 1. Validate destination account exists
            try (PreparedStatement checkDestStmt = conn.prepareStatement(checkDestSql)) {
                checkDestStmt.setString(1, toAccount);
                ResultSet rs = checkDestStmt.executeQuery();
                if (!rs.next() || rs.getInt(1) == 0) {
                    conn.rollback();
                    return new TransactionResult(false, "Destination account not found");
                }
            }

            // 2. Validate source balance
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, fromAccount);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    BigDecimal currentBalance = rs.getBigDecimal("balance");
                    if (currentBalance.compareTo(amount) < 0) {
                        conn.rollback();
                        return new TransactionResult(false, "Insufficient funds");
                    }
                } else {
                    conn.rollback();
                    return new TransactionResult(false, "Source account not found");
                }
            }

            // 3. Perform Transfer
            try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
                 PreparedStatement depositStmt = conn.prepareStatement(depositSql);
                 PreparedStatement logFromStmt = conn.prepareStatement(logFromSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement logToStmt = conn.prepareStatement(logToSql)) {

                // Withdraw
                withdrawStmt.setBigDecimal(1, amount);
                withdrawStmt.setString(2, fromAccount);
                withdrawStmt.executeUpdate();

                // Deposit
                depositStmt.setBigDecimal(1, amount);
                depositStmt.setString(2, toAccount);
                depositStmt.executeUpdate();

                // Log Source Transaction
                logFromStmt.setString(1, fromAccount);
                logFromStmt.setBigDecimal(2, amount.negate());
                logFromStmt.setString(3, toAccount);
                logFromStmt.setString(4, "COMPLETED");
                logFromStmt.executeUpdate();
                
                int sourceTxId = -1;
                try (ResultSet generatedKeys = logFromStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sourceTxId = generatedKeys.getInt(1);
                    }
                }
                logAudit(conn, sourceTxId, "COMPLETED", null);

                // Log Destination Transaction
                logToStmt.setString(1, toAccount);
                logToStmt.setBigDecimal(2, amount);
                logToStmt.setString(3, fromAccount);
                logToStmt.setString(4, "COMPLETED");
                logToStmt.executeUpdate();

                conn.commit();
                return new TransactionResult(true, sourceTxId, "Transfer successful");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                // Log the failure if we can (in a separate connection or after rollback if possible, 
                // but here we just return the error)
                return new TransactionResult(false, "Transfer failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TransactionResult(false, "System error: " + e.getMessage());
        }
    }
    
    private void logAudit(Connection conn, int transactionId, String status, String errorMessage) throws SQLException {
        if (transactionId == -1) return;
        
        String sql = "INSERT INTO transaction_audit_log (transaction_id, status, error_message) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.setString(2, status);
            pstmt.setString(3, errorMessage);
            pstmt.executeUpdate();
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
