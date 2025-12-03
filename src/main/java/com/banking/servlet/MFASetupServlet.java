package com.banking.servlet;

import com.banking.dao.OTPVerificationDAO;
import com.banking.dao.UserDAO;
import com.banking.model.User;
import com.banking.util.MFAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/mfa-setup")
public class MFASetupServlet extends HttpServlet {
    private OTPVerificationDAO otpDAO;
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
        otpDAO = new OTPVerificationDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // Generate a new secret for setup
        String secret = MFAUtil.generateTOTPSecret();
        String qrCodeUrl = MFAUtil.generateQRCodeURL(user.getUsername(), secret);
        
        request.setAttribute("secret", secret);
        request.setAttribute("qrCodeUrl", qrCodeUrl);
        request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String method = request.getParameter("method");
        String secret = request.getParameter("secret");
        String code = request.getParameter("code");
        
        boolean verified = false;
        
        if ("TOTP".equals(method)) {
            verified = MFAUtil.validateTOTPCode(secret, code);
        } else if ("EMAIL".equals(method)) {
            if (code == null || code.trim().isEmpty()) {
                // Step 1: Send OTP
                String otp = MFAUtil.generateEmailOTP();
                otpDAO.createOTP(user.getId(), otp, "SETUP");
                com.banking.util.EmailService.sendOTPEmail(user.getEmail(), otp);
                
                request.setAttribute("showEmailVerify", true);
                request.setAttribute("message", "OTP sent to " + user.getEmail());
                // Preserve secret/qr for other tabs if needed, though mostly irrelevant here
                request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
                return;
            } else {
                // Step 2: Verify
                verified = otpDAO.verifyOTP(user.getId(), code, "SETUP");
            }
        } else if ("SMS".equals(method)) {
             if (code == null || code.trim().isEmpty()) {
                // Step 1: Send OTP
                String otp = MFAUtil.generateEmailOTP();
                otpDAO.createOTP(user.getId(), otp, "SETUP");
                com.banking.util.SMSService.sendOTP(user.getPhoneNumber(), otp);
                
                request.setAttribute("showSMSVerify", true);
                request.setAttribute("message", "OTP sent to " + user.getPhoneNumber());
                request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
                return;
            } else {
                // Step 2: Verify
                verified = otpDAO.verifyOTP(user.getId(), code, "SETUP");
            }
        }
        
        if (verified) {
            boolean updated = userDAO.updateMFASettings(user.getId(), true, secret, method);
            if (updated) {
                // Update session user
                user.setMfaEnabled(true);
                user.setMfaSecret(secret);
                user.setMfaMethod(method);
                
                session.setAttribute("message", "MFA enabled successfully!");
                session.setAttribute("messageType", "success");
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("error", "Failed to update MFA settings.");
                request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Invalid verification code.");
            if ("EMAIL".equals(method)) request.setAttribute("showEmailVerify", true);
            if ("SMS".equals(method)) request.setAttribute("showSMSVerify", true);
            
            // Regenerate TOTP stuff just in case they switch back
            String newSecret = MFAUtil.generateTOTPSecret();
            request.setAttribute("secret", newSecret);
            request.setAttribute("qrCodeUrl", MFAUtil.generateQRCodeURL(user.getUsername(), newSecret));
            
            request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
        }
    }
}
