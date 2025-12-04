package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.model.Account;
import com.banking.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

@WebServlet("/createAccount")
public class CreateAccountServlet extends HttpServlet {
    private AccountDAO accountDAO;

    public void init() {
        accountDAO = new AccountDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String accountType = request.getParameter("accountType");
        BigDecimal initialDeposit = new BigDecimal(request.getParameter("initialDeposit"));

        // Generate random account number
        String accountNumber = "ACC" + (100000 + new Random().nextInt(900000));

        Account newAccount = new Account(accountNumber, user.getId(), initialDeposit, accountType);

        if (accountDAO.createAccount(newAccount)) {
            response.sendRedirect("dashboard?success=Account created successfully!");
        } else {
            request.setAttribute("error", "Failed to create account.");
            request.getRequestDispatcher("create_account.jsp").forward(request, response);
        }
    }
}
