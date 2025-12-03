# SMS Integration Guide

## Current Status: Console Simulation

The SMS OTP feature is currently in **SIMULATION MODE**. OTPs are printed to the **Tomcat Server Console** instead of being sent to actual phone numbers.

## How to Use (Current Implementation)

1. **Register** with a phone number (any 10-digit number)
2. **Enable SMS MFA** during setup
3. **Check the Tomcat Console** (server logs) for the OTP code
4. **Copy the OTP** and paste it into the verification form

## Example Console Output

```
==========================================
📱 SMS SIMULATION - CHECK CONSOLE 📱
==========================================
To: 1234567890
Message: Your Online Banking OTP is: 123456. Valid for 5 minutes.
==========================================

🔑 OTP CODE: 123456 🔑
(Check your Tomcat console for this code)
```

## How to Enable Real SMS (Optional)

To send actual SMS messages, you need to integrate with an SMS gateway service:

### Option 1: Twilio (Recommended)

1. **Sign up** for Twilio: https://www.twilio.com/
2. **Get credentials**: Account SID, Auth Token, and Phone Number
3. **Add dependency** to `pom.xml`:
   ```xml
   <dependency>
       <groupId>com.twilio.sdk</groupId>
       <artifactId>twilio</artifactId>
       <version>9.14.1</version>
   </dependency>
   ```
4. **Update SMSService.java**:
   ```java
   import com.twilio.Twilio;
   import com.twilio.rest.api.v2010.account.Message;
   import com.twilio.type.PhoneNumber;

   public class SMSService {
       private static final String ACCOUNT_SID = "your_account_sid";
       private static final String AUTH_TOKEN = "your_auth_token";
       private static final String FROM_NUMBER = "+1234567890"; // Your Twilio number

       static {
           Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
       }

       public static void sendOTP(String phoneNumber, String otp) {
           String message = "Your Online Banking OTP is: " + otp + ". Valid for 5 minutes.";
           
           Message.creator(
               new PhoneNumber("+91" + phoneNumber), // Add country code
               new PhoneNumber(FROM_NUMBER),
               message
           ).create();
       }
   }
   ```

### Option 2: AWS SNS

1. **Set up AWS account** and SNS service
2. **Add AWS SDK** dependency
3. **Configure credentials** in AWS credentials file
4. **Update SMSService** to use AWS SNS API

### Option 3: Other Services

- **MSG91** (India)
- **Nexmo/Vonage**
- **Plivo**
- **TextLocal**

## Cost Considerations

- **Twilio**: ~$0.0075 per SMS (varies by country)
- **AWS SNS**: ~$0.00645 per SMS
- **MSG91**: ₹0.15-0.25 per SMS (India)

## Security Note

⚠️ **Never commit API credentials to version control!** Use environment variables or configuration files that are excluded from Git.

## Current Recommendation

For **development and testing**, the console simulation is sufficient and free. For **production deployment**, integrate with a real SMS gateway service.
