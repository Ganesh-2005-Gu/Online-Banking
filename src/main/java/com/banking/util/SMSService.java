package com.banking.util;

public class SMSService {

    public static void sendSMS(String phoneNumber, String message) {
        // In a real application, this would integrate with an SMS gateway like Twilio.
        // For this project, we will simulate sending an SMS by printing to the console.
        
        System.out.println("\n==========================================");
        System.out.println("📱 SMS SIMULATION - CHECK CONSOLE 📱");
        System.out.println("==========================================");
        System.out.println("To: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("==========================================\n");
    }
    
    public static void sendOTP(String phoneNumber, String otp) {
        String message = "Your Online Banking OTP is: " + otp + ". Valid for 5 minutes.";
        sendSMS(phoneNumber, message);
        
        // Also print just the OTP for easy copying
        System.out.println("🔑 OTP CODE: " + otp + " 🔑");
        System.out.println("(Check your Tomcat console for this code)\n");
    }
}
