package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.dao.UserDAO;
import com.banking.model.Account;
import com.banking.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;
    private AccountDAO accountDAO;

    public void init() {
        userDAO = new UserDAO();
        accountDAO = new AccountDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"ADMIN".equals(user.getRole())) {
            response.sendRedirect("dashboard");
            return;
        }

        String action = request.getParameter("action");
        if ("search".equals(action)) {
            String accountNumber = request.getParameter("accountNumber");
            Account account = accountDAO.getAccountByNumber(accountNumber);
            request.setAttribute("searchedAccount", account);
            if (account != null) {
                User accountOwner = userDAO.getUserById(account.getUserId());
                request.setAttribute("accountOwner", accountOwner);
            }
        } else {
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
        }

        request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
    }
}
