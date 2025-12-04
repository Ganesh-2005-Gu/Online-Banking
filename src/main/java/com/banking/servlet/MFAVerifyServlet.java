package com.banking.servlet;

import com.banking.dao.OTPVerificationDAO;
import com.banking.model.User;
import com.banking.util.EmailService;
import com.banking.util.MFAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/mfa-verify")
public class MFAVerifyServlet extends HttpServlet {
    private OTPVerificationDAO otpDAO;

    public void init() {
        otpDAO = new OTPVerificationDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tempUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        User tempUser = (User) session.getAttribute("tempUser");
        
        // If method is EMAIL and no OTP sent yet (or resend requested), send it
        if ("EMAIL".equals(tempUser.getMfaMethod()) && request.getParameter("sent") == null) {
            String otp = MFAUtil.generateEmailOTP();
            otpDAO.createOTP(tempUser.getId(), otp, "LOGIN");
            EmailService.sendOTPEmail(tempUser.getEmail(), otp);
            request.setAttribute("message", "OTP sent to your email.");
        } else if ("SMS".equals(tempUser.getMfaMethod()) && request.getParameter("sent") == null) {
            String otp = MFAUtil.generateEmailOTP(); // Reuse same numeric OTP generator
            otpDAO.createOTP(tempUser.getId(), otp, "LOGIN");
            com.banking.util.SMSService.sendOTP(tempUser.getPhoneNumber(), otp);
            request.setAttribute("message", "OTP sent to your phone.");
        }

        request.getRequestDispatcher("mfa_verify.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tempUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User tempUser = (User) session.getAttribute("tempUser");
        String code = request.getParameter("code");
        
        System.out.println("=== MFA Verify Servlet ===");
        System.out.println("User: " + tempUser.getUsername() + " (ID: " + tempUser.getId() + ")");
        System.out.println("MFA Method: " + tempUser.getMfaMethod());
        System.out.println("Received code parameter: [" + code + "]");
        System.out.println("Code length: " + (code != null ? code.length() : "null"));
        
        boolean verified = false;

        // Only EMAIL and SMS are supported now
        if ("EMAIL".equals(tempUser.getMfaMethod()) || "SMS".equals(tempUser.getMfaMethod())) {
            verified = otpDAO.verifyOTP(tempUser.getId(), code, "LOGIN");
            System.out.println("OTP verification result: " + verified);
        } else {
            System.out.println("Unsupported MFA method: " + tempUser.getMfaMethod());
        }

        if (verified) {
            System.out.println("Verification successful! Redirecting user...");
            // Promote tempUser to full user session
            session.removeAttribute("tempUser");
            session.setAttribute("user", tempUser);
            
            if ("ADMIN".equals(tempUser.getRole())) {
                response.sendRedirect("admin_dashboard.jsp");
            } else {
                response.sendRedirect("dashboard");
            }
        } else {
            System.out.println("Verification failed!");
            request.setAttribute("error", "Invalid or expired code.");
            request.getRequestDispatcher("mfa_verify.jsp").forward(request, response);
        }
        System.out.println("=== End MFA Verify ===");
    }
}
