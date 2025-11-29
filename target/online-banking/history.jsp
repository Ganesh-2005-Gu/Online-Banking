<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Transaction History - Online Banking</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body class="bg-history">
            <div class="page-background"></div>
            <div class="particles" id="particles"></div>

            <header>
                <div class="container">
                    <h1>💳 Welcome, ${sessionScope.user.fullName}</h1>
                    <nav>
                        <a href="dashboard">Dashboard</a>
                        <a href="create_account.jsp">New Account</a>
                        <a href="logout">Logout</a>
                    </nav>
                </div>
            </header>

            <div class="container">
                <div class="card">
                    <h2>📜 Transaction History - ${accountNumber}</h2>
                    <c:choose>
                        <c:when test="${not empty transactions}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Date & Time</th>
                                        <th>Type</th>
                                        <th>Amount</th>
                                        <th>Related Account</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="txn" items="${transactions}">
                                        <tr>
                                            <td>${txn.timestamp}</td>
                                            <td>
                                                <span
                                                    style="background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.85rem;">
                                                    ${txn.type}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${txn.amount >= 0}">
                                                        <span style="font-weight: 700; color: #27ae60;">
                                                            +$${txn.amount}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="font-weight: 700; color: #e74c3c;">
                                                            $${txn.amount}
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${txn.relatedAccount != null ? txn.relatedAccount : '-'}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div style="text-align: center; padding: 40px; color: #666;">
                                <p style="font-size: 1.2rem;">No transactions found for this account.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <p style="margin-top: 30px; text-align: center;">
                        <a href="dashboard"><button>← Back to Dashboard</button></a>
                    </p>
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