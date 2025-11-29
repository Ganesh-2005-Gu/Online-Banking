<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - Online Banking</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body class="bg-login">
        <div class="page-background"></div>
        <div class="particles" id="particles"></div>

        <header>
            <div class="container">
                <h1>💳 SecureBank Online</h1>
                <nav>
                    <a href="register.jsp">Create Account</a>
                </nav>
            </div>
        </header>

        <div class="container login-container">
            <div class="card">
                <h2>Welcome Back!</h2>
                <% if (request.getAttribute("error") !=null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                    <% } %>
                        <% if (request.getParameter("success") !=null) { %>
                            <div class="alert alert-success">
                                <%= request.getParameter("success") %>
                            </div>
                            <% } %>

                                <form action="login" method="post">
                                    <div class="form-group">
                                        <label for="username">Username</label>
                                        <input type="text" id="username" name="username"
                                            placeholder="Enter your username" required autofocus>
                                    </div>
                                    <div class="form-group">
                                        <label for="password">Password</label>
                                        <input type="password" id="password" name="password"
                                            placeholder="Enter your password" required>
                                    </div>
                                    <button type="submit">Sign In</button>
                                </form>
                                <p style="margin-top: 20px; text-align: center; color: #666;">
                                    Don't have an account? <a href="register.jsp"
                                        style="color: #667eea; font-weight: 600;">Register here</a>
                                </p>
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
        </script>
    </body>

    </html>