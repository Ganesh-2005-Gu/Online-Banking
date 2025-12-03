package com.banking.servlet;

import com.banking.dao.UserDAO;
import com.banking.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.loginUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            
            if (user.isMfaEnabled()) {
                // MFA is enabled, redirect to verification
                session.setAttribute("tempUser", user);
                response.sendRedirect("mfa-verify");
            } else {
                // MFA not enabled, proceed to dashboard (or setup)
                session.setAttribute("user", user);
                if ("ADMIN".equals(user.getRole())) {
                    response.sendRedirect("admin_dashboard.jsp");
                } else {
                    // Optional: Redirect to MFA setup if not enabled
                    // response.sendRedirect("mfa-setup");
                    response.sendRedirect("dashboard");
                }
            }
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
