package com.banking.dao;

import com.banking.util.DBConnection;

import java.sql.*;

public class OTPVerificationDAO {

    public boolean createOTP(int userId, String otpCode, String purpose) {
        String sql = "INSERT INTO otp_verification (user_id, otp_code, expires_at, purpose) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE), ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, otpCode);
            pstmt.setString(3, purpose);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyOTP(int userId, String otpCode, String purpose) {
        String sql = "SELECT id FROM otp_verification WHERE user_id = ? AND otp_code = ? AND purpose = ? AND verified = FALSE AND expires_at > NOW()";
        String updateSql = "UPDATE otp_verification SET verified = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(sql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setString(2, otpCode);
                checkStmt.setString(3, purpose);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int otpId = rs.getInt("id");
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, otpId);
                            updateStmt.executeUpdate();
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
