package com.banking.dao;

import com.banking.util.DBConnection;

import java.sql.*;

public class OTPVerificationDAO {

    public boolean createOTP(int userId, String otpCode, String purpose) {
        String sql = "INSERT INTO otp_verification (user_id, otp_code, expires_at, purpose) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE), ?)";
        
        System.out.println("=== Creating OTP ===");
        System.out.println("User ID: " + userId);
        System.out.println("OTP Code: [" + otpCode + "]");
        System.out.println("Purpose: " + purpose);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, otpCode);
            pstmt.setString(3, purpose);

            int result = pstmt.executeUpdate();
            System.out.println("OTP creation result: " + (result > 0 ? "SUCCESS" : "FAILED"));
            System.out.println("=== End Creating OTP ===");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error creating OTP:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyOTP(int userId, String otpCode, String purpose) {
        String sql = "SELECT id, otp_code, expires_at FROM otp_verification WHERE user_id = ? AND purpose = ? AND verified = FALSE AND expires_at > NOW()";
        String updateSql = "UPDATE otp_verification SET verified = TRUE WHERE id = ?";

        System.out.println("=== OTP Verification Debug ===");
        System.out.println("User ID: " + userId);
        System.out.println("Entered OTP Code: [" + otpCode + "]");
        System.out.println("Purpose: " + purpose);

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(sql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setString(2, purpose);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int otpId = rs.getInt("id");
                        String storedOtp = rs.getString("otp_code");
                        Timestamp expiresAt = rs.getTimestamp("expires_at");
                        
                        System.out.println("Found OTP in DB: [" + storedOtp + "]");
                        System.out.println("Expires at: " + expiresAt);
                        System.out.println("OTP Match: " + otpCode.equals(storedOtp));
                        System.out.println("OTP Trim Match: " + otpCode.trim().equals(storedOtp.trim()));
                        
                        // Compare with trimmed values to handle whitespace issues
                        if (otpCode.trim().equals(storedOtp.trim())) {
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setInt(1, otpId);
                                updateStmt.executeUpdate();
                                System.out.println("OTP verified successfully!");
                                return true;
                            }
                        } else {
                            System.out.println("OTP mismatch!");
                        }
                    } else {
                        System.out.println("No matching OTP found in database");
                        // Check if there are any OTPs for this user
                        String debugSql = "SELECT id, otp_code, verified, expires_at FROM otp_verification WHERE user_id = ? AND purpose = ? ORDER BY created_at DESC LIMIT 3";
                        try (PreparedStatement debugStmt = conn.prepareStatement(debugSql)) {
                            debugStmt.setInt(1, userId);
                            debugStmt.setString(2, purpose);
                            try (ResultSet debugRs = debugStmt.executeQuery()) {
                                System.out.println("Recent OTPs for this user:");
                                while (debugRs.next()) {
                                    System.out.println("  ID: " + debugRs.getInt("id") + 
                                                     ", Code: [" + debugRs.getString("otp_code") + "]" +
                                                     ", Verified: " + debugRs.getBoolean("verified") +
                                                     ", Expires: " + debugRs.getTimestamp("expires_at"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during OTP verification:");
            e.printStackTrace();
        }
        System.out.println("=== End OTP Verification ===");
        return false;
    }

    public void cleanupExpiredOTPs() {
        String sql = "DELETE FROM otp_verification WHERE expires_at < NOW()";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
