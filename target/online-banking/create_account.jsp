<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create Account - Online Banking</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body class="bg-create">
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

        <div class="container login-container">
            <div class="card">
                <h2>🏦 Open New Account</h2>
                <% if (request.getAttribute("error") !=null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                    <% } %>

                        <form action="createAccount" method="post">
                            <div class="form-group">
                                <label for="accountType">Account Type</label>
                                <select name="accountType" id="accountType" required>
                                    <option value="SAVINGS">💰 Savings Account</option>
                                    <option value="CURRENT">💼 Current Account</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="initialDeposit">Initial Deposit</label>
                                <input type="number" id="initialDeposit" name="initialDeposit" step="0.01" min="0"
                                    placeholder="Enter initial deposit amount" required>
                            </div>
                            <button type="submit">Create Account</button>
                        </form>
                        <p style="margin-top: 20px; text-align: center;">
                            <a href="dashboard" style="color: #667eea; font-weight: 600; text-decoration: none;">← Back
                                to Dashboard</a>
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