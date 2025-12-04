<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Dashboard - Online Banking</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body class="bg-dashboard">
            <div class="page-background"></div>
            <div class="particles" id="particles"></div>

            <header>
                <div class="container">
                    <h1>💳 Welcome, ${sessionScope.user.fullName}</h1>
                    <nav>
                        <a href="dashboard">Dashboard</a>
                        <a href="create_account.jsp">New Account</a>
                        <a href="mfa-setup">Security (MFA)</a>
                        <a href="logout">Logout</a>
                    </nav>
                </div>
            </header>

            <div class="container">
                <% if (request.getAttribute("message") !=null) { %>
                    <div class="alert alert-success">
                        <%= request.getAttribute("message") %>
                    </div>
                    <% } %>
                        <% if (request.getParameter("success") !=null) { %>
                            <div class="alert alert-success">
                                <%= request.getParameter("success") %>
                            </div>
                            <% } %>
                                <% if (request.getParameter("message") !=null) { %>
                                    <div class="alert alert-info">
                                        <%= request.getParameter("message") %>
                                    </div>
                                    <% } %>

                                        <div class="dashboard-grid">
                                            <!-- Accounts List -->
                                            <div class="card" style="grid-column: span 2;">
                                                <h2>📊 Your Accounts</h2>
                                                <c:choose>
                                                    <c:when test="${not empty accounts}">
                                                        <table>
                                                            <thead>
                                                                <tr>
                                                                    <th>Account Number</th>
                                                                    <th>Type</th>
                                                                    <th>Balance</th>
                                                                    <th>Actions</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="acc" items="${accounts}">
                                                                    <tr>
                                                                        <td><strong>${acc.accountNumber}</strong></td>
                                                                        <td><span
                                                                                style="background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.85rem;">${acc.accountType}</span>
                                                                        </td>
                                                                        <td><span class="balance-display"
                                                                                style="font-size: 1.5rem;">$${acc.balance}</span>
                                                                        </td>
                                                                        <td>
                                                                            <button
                                                                                onclick="openTransactionModal('${acc.accountNumber}')"
                                                                                style="padding: 8px 16px; font-size: 0.9rem;">Transact</button>
                                                                            <a href="history?accountNumber=${acc.accountNumber}"
                                                                                style="margin-left: 10px; color: #667eea; font-weight: 600; text-decoration: none;">History</a>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="text-align: center; padding: 40px; color: #666;">
                                                            <p style="font-size: 1.2rem; margin-bottom: 20px;">You don't
                                                                have any accounts yet.</p>
                                                            <a href="create_account.jsp"><button>Create Your First
                                                                    Account</button></a>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <!-- Transaction Form -->
                                            <div class="card">
                                                <h2>💸 Quick Transaction</h2>
                                                <form action="transaction" method="post">
                                                    <div class="form-group">
                                                        <label for="action">Action</label>
                                                        <select name="action" id="action"
                                                            onchange="toggleTransferField()">
                                                            <option value="deposit">💰 Deposit</option>
                                                            <option value="withdraw">💵 Withdraw</option>
                                                            <option value="transfer">🔄 Transfer</option>
                                                        </select>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="accountNumber">From Account</label>
                                                        <select name="accountNumber" id="accountNumber" required>
                                                            <c:forEach var="acc" items="${accounts}">
                                                                <option value="${acc.accountNumber}">
                                                                    ${acc.accountNumber} ($${acc.balance})</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <div class="form-group" id="toAccountField" style="display: none;">
                                                        <label for="toAccount">To Account Number</label>
                                                        <input type="text" name="toAccount" id="toAccount"
                                                            placeholder="Enter recipient account">
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="amount">Amount</label>
                                                        <input type="number" name="amount" id="amount" step="0.01"
                                                            min="0.01" placeholder="0.00" required>
                                                    </div>
                                                    <button type="submit">Submit Transaction</button>
                                                </form>
                                            </div>
                                        </div>
            </div>

            <script>
                // Create floating particles
                const particlesContainer = document.getElementById('particles');
                for (let i = 0; i < 50; i++) {
                    const particle = document.createElement('div');
                    particle.className = 'particle';
                    particle.style.left = Math.random() * 100 + '%';
                    particle.style.animationDelay = Math.random() * 20 + 's';
                    particle.style.animationDuration = (Math.random() * 10 + 15) + 's';
                    particlesContainer.appendChild(particle);
                }

                function toggleTransferField() {
                    var action = document.getElementById("action").value;
                    var toAccountField = document.getElementById("toAccountField");
                    if (action === "transfer") {
                        toAccountField.style.display = "block";
                        document.getElementById("toAccount").required = true;
                    } else {
                        toAccountField.style.display = "none";
                        document.getElementById("toAccount").required = false;
                    }
                }

                function openTransactionModal(accountNumber) {
                    document.getElementById("accountNumber").value = accountNumber;
                    window.scrollTo({ top: document.querySelector('.card:last-child').offsetTop - 100, behavior: 'smooth' });
                }
            </script>
        </body>

        </html>