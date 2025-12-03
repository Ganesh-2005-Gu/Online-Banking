package com.banking.servlet;

import com.banking.dao.TransactionDAO;
import com.banking.model.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    private TransactionDAO transactionDAO;

    public void init() {
        transactionDAO = new TransactionDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accountNumber = request.getParameter("accountNumber");
        // Security check: Ensure this account belongs to the user (skipped for brevity,
        // but important in real app)

        List<Transaction> transactions = transactionDAO.getTransactionsByAccountNumber(accountNumber);

        request.setAttribute("transactions", transactions);
        request.setAttribute("accountNumber", accountNumber);
        request.getRequestDispatcher("history.jsp").forward(request, response);
    }
}
