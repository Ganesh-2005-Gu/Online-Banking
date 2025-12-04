package com.banking.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Sorry, unable to find email.properties");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void sendOTPEmail(String toEmail, String otpCode) {
        String subject = "Your OTP Code - Online Banking";
        String body = "Your One-Time Password (OTP) for Online Banking is: " + otpCode + "\n\n" +
                      "This code will expire in 5 minutes.\n" +
                      "If you did not request this, please ignore this email.";
        
        // FOR TESTING: Print OTP to console
        System.out.println("==========================================");
        System.out.println("TESTING MODE - OTP for " + toEmail + ": " + otpCode);
        System.out.println("==========================================");
        
        sendEmail(toEmail, subject, body);
    }

    public static void sendEmail(String toEmail, String subject, String body) {
        final String username = properties.getProperty("mail.username");
        final String password = properties.getProperty("mail.password");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            System.err.println("CRITICAL ERROR: Failed to send email to " + toEmail);
            System.err.println("Check your email.properties configuration.");
            e.printStackTrace();
        }
    }
}
