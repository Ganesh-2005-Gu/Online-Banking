package com.banking.servlet;

import com.banking.dao.UserDAO;
import com.banking.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setRole("USER"); // Default role
        newUser.setMfaEnabled(false);
        newUser.setMfaMethod("NONE");

        if (userDAO.registerUser(newUser)) {
            // Auto-login after registration
            User registeredUser = userDAO.loginUser(username, password);
            if (registeredUser != null) {
                jakarta.servlet.http.HttpSession session = request.getSession();
                session.setAttribute("user", registeredUser);
                
                // Set success message
                session.setAttribute("message", "Welcome! Your account has been created successfully. You can add MFA security from your dashboard.");
                session.setAttribute("messageType", "success");
                
                // Go directly to dashboard - MFA is optional
                response.sendRedirect("dashboard");
            } else {
                response.sendRedirect("index.jsp?success=Registration successful! Please login.");
            }
        } else {
            request.setAttribute("error", "Registration failed. Username might be taken.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
