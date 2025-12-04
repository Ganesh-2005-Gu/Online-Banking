<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>MFA Setup - Online Banking</title>
            <link rel="stylesheet" href="css/style.css">
            <style>
                .mfa-container {
                    max-width: 800px;
                    margin: 50px auto;
                    background: rgba(255, 255, 255, 0.95);
                    padding: 30px;
                    border-radius: 15px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                }

                .mfa-options {
                    display: flex;
                    gap: 20px;
                    margin-bottom: 30px;
                }

                .mfa-option {
                    flex: 1;
                    padding: 20px;
                    border: 2px solid #e0e0e0;
                    border-radius: 10px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    text-align: center;
                }

                .mfa-option:hover,
                .mfa-option.active {
                    border-color: #4a90e2;
                    background: rgba(74, 144, 226, 0.05);
                }

                .mfa-option h3 {
                    margin-top: 0;
                    color: #333;
                }

                .qr-code {
                    text-align: center;
                    margin: 20px 0;
                }

                .qr-code img {
                    border: 1px solid #ddd;
                    padding: 10px;
                    background: white;
                    border-radius: 5px;
                }

                .secret-key {
                    background: #f5f5f5;
                    padding: 10px;
                    border-radius: 5px;
                    font-family: monospace;
                    text-align: center;
                    margin: 10px 0;
                    letter-spacing: 2px;
                }

                .step-number {
                    display: inline-block;
                    width: 24px;
                    height: 24px;
                    background: #4a90e2;
                    color: white;
                    border-radius: 50%;
                    text-align: center;
                    line-height: 24px;
                    margin-right: 10px;
                }

                .hidden {
                    display: none;
                }
            </style>
        </head>
        </head>

        <body class="bg-mfa">
            <div class="page-background"></div>
            <div class="particles" id="particles"></div>

            <div class="mfa-container">
                <h2>Set Up Multi-Factor Authentication</h2>
                <p>Enhance your account security by enabling MFA. Choose your preferred method below.</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <div class="mfa-options">
                    <div class="mfa-option active" onclick="selectMethod('EMAIL')">
                        <h3>📧 Email OTP</h3>
                        <p>Receive a one-time password via email.</p>
                    </div>
                    <div class="mfa-option" onclick="selectMethod('SMS')">
                        <h3>📱 SMS OTP</h3>
                        <p>Receive a code via text message.</p>
                    </div>
                </div>



                <!-- Email Setup Section (Default) -->
                <div id="email-section">
                    <c:choose>
                        <c:when test="${showEmailVerify}">
                            <div class="step">
                                <h4><span class="step-number">2</span>Verify Email</h4>
                                <p>Enter the OTP sent to <strong>${sessionScope.user.email}</strong></p>
                                <form action="mfa-setup" method="post">
                                    <input type="hidden" name="method" value="EMAIL">
                                    <div class="form-group">
                                        <input type="text" name="code" pattern="[0-9]{6}" required placeholder="000000"
                                            maxlength="6"
                                            style="font-size: 1.2em; letter-spacing: 2px; text-align: center; width: 200px;">
                                    </div>
                                    <button type="submit" class="btn-primary">Verify & Enable</button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="step">
                                <h4><span class="step-number">1</span>Confirm Email Address</h4>
                                <p>We will send OTPs to your registered email address:
                                    <strong>${sessionScope.user.email}</strong>
                                </p>
                            </div>
                            <div class="step">
                                <h4><span class="step-number">2</span>Send Verification Code</h4>
                                <p>Click below to receive an OTP and verify your email.</p>
                                <form action="mfa-setup" method="post">
                                    <input type="hidden" name="method" value="EMAIL">
                                    <button type="submit" class="btn-primary">Send OTP</button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- SMS Setup Section -->
                <div id="sms-section" class="hidden">
                    <c:choose>
                        <c:when test="${showSMSVerify}">
                            <div class="step">
                                <h4><span class="step-number">2</span>Verify Phone Number</h4>
                                <p>Enter the OTP sent to <strong>${sessionScope.user.phoneNumber}</strong></p>
                                <form action="mfa-setup" method="post">
                                    <input type="hidden" name="method" value="SMS">
                                    <div class="form-group">
                                        <input type="text" name="code" pattern="[0-9]{6}" required placeholder="000000"
                                            maxlength="6"
                                            style="font-size: 1.2em; letter-spacing: 2px; text-align: center; width: 200px;">
                                    </div>
                                    <button type="submit" class="btn-primary">Verify & Enable</button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="step">
                                <h4><span class="step-number">1</span>Confirm Phone Number</h4>
                                <p>We will send OTPs to your registered phone number:
                                    <strong>${sessionScope.user.phoneNumber}</strong>
                                </p>
                            </div>
                            <div class="step">
                                <h4><span class="step-number">2</span>Send Verification Code</h4>
                                <p>Click below to receive an OTP and verify your phone number.</p>
                                <form action="mfa-setup" method="post">
                                    <input type="hidden" name="method" value="SMS">
                                    <button type="submit" class="btn-primary">Send OTP</button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="margin-top: 30px; text-align: center;">
                    <a href="dashboard" style="color: #666; text-decoration: none;">Skip for now</a>
                </div>
            </div>

            <script>
                function selectMethod(method) {
                    document.querySelectorAll('.mfa-option').forEach(el => el.classList.remove('active'));
                    document.getElementById('email-section').classList.add('hidden');
                    document.getElementById('sms-section').classList.add('hidden');

                    if (method === 'EMAIL') {
                        document.querySelector('.mfa-option:nth-child(1)').classList.add('active');
                        document.getElementById('email-section').classList.remove('hidden');
                    } else if (method === 'SMS') {
                        document.querySelector('.mfa-option:nth-child(2)').classList.add('active');
                        document.getElementById('sms-section').classList.remove('hidden');
                    }
                }

                // Auto-select method if verification is in progress
                var showEmailVerify = "${showEmailVerify}";
                var showSMSVerify = "${showSMSVerify}";

                if (showEmailVerify === "true") {
                    selectMethod('EMAIL');
                } else if (showSMSVerify === "true") {
                    selectMethod('SMS');
                }

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