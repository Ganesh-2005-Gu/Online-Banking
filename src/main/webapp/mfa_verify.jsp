<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Identity - Online Banking</title>
            <link rel="stylesheet" href="css/style.css">
            <style>
                .verify-container {
                    max-width: 400px;
                    margin: 100px auto;
                    background: rgba(255, 255, 255, 0.95);
                    padding: 40px;
                    border-radius: 15px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }

                .verify-icon {
                    font-size: 48px;
                    margin-bottom: 20px;
                    color: #4a90e2;
                }

                .otp-input {
                    font-size: 24px;
                    letter-spacing: 5px;
                    text-align: center;
                    width: 200px;
                    padding: 10px;
                    margin: 20px 0;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                }

                .otp-input:focus {
                    border-color: #4a90e2;
                    outline: none;
                }
            </style>
        </head>

        <body class="bg-mfa">
            <div class="page-background"></div>
            <div class="particles" id="particles"></div>

            <div class="verify-container">
                <div class="verify-icon">🔒</div>
                <h2>Two-Factor Authentication</h2>

                <c:if test="${sessionScope.tempUser.mfaMethod == 'TOTP'}">
                    <p>Please enter the 6-digit code from your authenticator app.</p>
                </c:if>
                <c:if test="${sessionScope.tempUser.mfaMethod == 'EMAIL'}">
                    <p>We sent a verification code to <strong>${sessionScope.tempUser.email}</strong></p>
                </c:if>
                <c:if test="${sessionScope.tempUser.mfaMethod == 'SMS'}">
                    <p>We sent a verification code to <strong>${sessionScope.tempUser.phoneNumber}</strong></p>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-error" style="color: red; margin-bottom: 15px;">${error}</div>
                </c:if>
                <c:if test="${not empty message}">
                    <div class="alert alert-success" style="color: green; margin-bottom: 15px;">${message}</div>
                </c:if>

                <form action="mfa-verify" method="post">
                    <input type="text" name="code" class="otp-input" pattern="[0-9]{6}" maxlength="6" required
                        placeholder="000000" autofocus>
                    <br>
                    <button type="submit" class="btn-primary" style="width: 100%;">Verify</button>
                </form>

                <c:if test="${sessionScope.tempUser.mfaMethod == 'EMAIL' || sessionScope.tempUser.mfaMethod == 'SMS'}">
                    <div style="margin-top: 20px;">
                        <a href="mfa-verify?sent=false" style="color: #666; font-size: 0.9em;">Resend Code</a>
                    </div>
                </c:if>

                <div style="margin-top: 20px;">
                    <a href="index.jsp" style="color: #999; text-decoration: none; font-size: 0.9em;">Back to Login</a>
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