package com.banking.servlet;

import com.banking.dao.TransactionDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    private TransactionDAO transactionDAO;

    public void init() {
        transactionDAO = new TransactionDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");
        String accountNumber = request.getParameter("accountNumber");
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));

        com.banking.model.TransactionResult result = null;

        if ("deposit".equals(action)) {
            result = transactionDAO.deposit(accountNumber, amount);
        } else if ("withdraw".equals(action)) {
            result = transactionDAO.withdraw(accountNumber, amount);
        } else if ("transfer".equals(action)) {
            String toAccount = request.getParameter("toAccount");
            result = transactionDAO.transfer(accountNumber, toAccount, amount);
        }

        String message = result != null ? result.getErrorMessage() : "Unknown action";
        // response.sendRedirect("dashboard?message=" + java.net.URLEncoder.encode(message, "UTF-8"));
        
        // Use session attribute for message to avoid URL encoding issues and cleaner URL
        session.setAttribute("message", message);
        session.setAttribute("messageType", result != null && result.isSuccess() ? "success" : "error");
        response.sendRedirect("dashboard");
    }
}
