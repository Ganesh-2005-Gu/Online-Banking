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

        // Just forward to the setup page - no need to generate TOTP secrets
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
        String code = request.getParameter("code");
        
        boolean verified = false;
        
        if ("EMAIL".equals(method)) {
            if (code == null || code.trim().isEmpty()) {
                // Step 1: Send OTP
                String otp = MFAUtil.generateEmailOTP();
                otpDAO.createOTP(user.getId(), otp, "SETUP");
                com.banking.util.EmailService.sendOTPEmail(user.getEmail(), otp);
                
                request.setAttribute("showEmailVerify", true);
                request.setAttribute("message", "OTP sent to " + user.getEmail());
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
            // For EMAIL/SMS, we don't need a secret, just use null
            boolean updated = userDAO.updateMFASettings(user.getId(), true, null, method);
            if (updated) {
                // Update session user
                user.setMfaEnabled(true);
                user.setMfaSecret(null);
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
            
            request.getRequestDispatcher("mfa_setup.jsp").forward(request, response);
        }
    }
}
