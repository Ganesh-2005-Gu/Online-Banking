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

        boolean success = false;
        String message = "";

        if ("deposit".equals(action)) {
            success = transactionDAO.deposit(accountNumber, amount);
            message = success ? "Deposit successful!" : "Deposit failed.";
        } else if ("withdraw".equals(action)) {
            success = transactionDAO.withdraw(accountNumber, amount);
            message = success ? "Withdrawal successful!" : "Withdrawal failed. Insufficient funds?";
        } else if ("transfer".equals(action)) {
            String toAccount = request.getParameter("toAccount");
            success = transactionDAO.transfer(accountNumber, toAccount, amount);
            message = success ? "Transfer successful!" : "Transfer failed. Check balance or account details.";
        }

        // request.setAttribute("message", message);
        // request.getRequestDispatcher("dashboard").forward(request, response);
        response.sendRedirect("dashboard?message=" + java.net.URLEncoder.encode(message, "UTF-8"));
    }
}
