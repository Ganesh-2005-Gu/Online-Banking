<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Admin Dashboard - Online Banking</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body class="bg-admin">
            <div class="page-background"></div>
            <div class="particles" id="particles"></div>

            <header>
                <div class="container">
                    <h1>👑 Admin Dashboard</h1>
                    <nav>
                        <a href="admin">Admin Panel</a>
                        <a href="dashboard">My Dashboard</a>
                        <a href="logout">Logout</a>
                    </nav>
                </div>
            </header>

            <div class="container">
                <div class="dashboard-grid">
                    <!-- Account Search -->
                    <div class="card">
                        <h2>🔍 Search Account</h2>
                        <form action="admin" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="form-group">
                                <label for="accountNumber">Account Number</label>
                                <input type="text" id="accountNumber" name="accountNumber"
                                    placeholder="Enter account number" required>
                            </div>
                            <button type="submit">Search</button>
                        </form>

                        <c:if test="${not empty searchedAccount}">
                            <div
                                style="margin-top: 30px; padding: 20px; background: linear-gradient(135deg, #667eea, #764ba2); border-radius: 12px; color: white;">
                                <h3 style="margin-top: 0;">Account Details</h3>
                                <p><strong>Account Number:</strong> ${searchedAccount.accountNumber}</p>
                                <p><strong>Type:</strong> ${searchedAccount.accountType}</p>
                                <p><strong>Balance:</strong> $${searchedAccount.balance}</p>
                                <c:if test="${not empty accountOwner}">
                                    <p><strong>Owner:</strong> ${accountOwner.fullName}</p>
                                    <p><strong>Email:</strong> ${accountOwner.email}</p>
                                </c:if>
                            </div>
                        </c:if>
                    </div>

                    <!-- All Users -->
                    <div class="card" style="grid-column: span 2;">
                        <h2>👥 All Users</h2>
                        <c:if test="${not empty users}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Full Name</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.id}</td>
                                            <td><strong>${user.username}</strong></td>
                                            <td>${user.fullName}</td>
                                            <td>${user.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.role == 'ADMIN'}">
                                                        <span
                                                            style="background: linear-gradient(135deg, #f093fb, #f5576c); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.85rem;">
                                                            ${user.role}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span
                                                            style="background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.85rem;">
                                                            ${user.role}
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </div>
                </div>
            </div>

            <script>
                const particlesContainer = document.getElementById('particles');
                for (let i = 0; i < 50; i++) {
                    const particle = document.createElement('div');
                    particle.className = 'particle';
                    particle.style.left = Math.random() * 100 + '%';
                    particle.style.animationDelay = Math.random() * 20 + 's';
                    particle.style.animationDuration = (Math.random() * 10 + 15) + 's';
                    particlesContainer.appendChild(particle);
                }
            </script>
        </body>

        </html>