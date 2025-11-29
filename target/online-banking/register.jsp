<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register - Online Banking</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body class="bg-register">
        <div class="page-background"></div>
        <div class="particles" id="particles"></div>

        <header>
            <div class="container">
                <h1>💳 SecureBank Online</h1>
                <nav>
                    <a href="index.jsp">Sign In</a>
                </nav>
            </div>
        </header>

        <div class="container login-container">
            <div class="card">
                <h2>Create Your Account</h2>
                <% if (request.getAttribute("error") !=null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                    <% } %>

                        <form action="register" method="post">
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" name="fullName" placeholder="John Doe" required
                                    autofocus>
                            </div>
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" name="email" placeholder="john@example.com" required>
                            </div>
                            <div class="form-group">
                                <label for="username">Username</label>
                                <input type="text" id="username" name="username" placeholder="Choose a username"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="password">Password</label>
                                <input type="password" id="password" name="password"
                                    placeholder="Create a strong password" required>
                            </div>
                            <button type="submit">Create Account</button>
                        </form>
                        <p style="margin-top: 20px; text-align: center; color: #666;">
                            Already have an account? <a href="index.jsp" style="color: #667eea; font-weight: 600;">Sign
                                in here</a>
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