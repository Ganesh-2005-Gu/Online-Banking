# Gmail Email OTP Setup Guide

## Error: "Username and Password not accepted"

This error means Gmail is rejecting your login credentials. Gmail requires **App Passwords** for third-party applications like this banking system.

## Solution: Create a Gmail App Password

### Step 1: Enable 2-Step Verification

1. Go to your **Google Account**: https://myaccount.google.com/
2. Click on **Security** (left sidebar)
3. Under "How you sign in to Google", click **2-Step Verification**
4. Follow the prompts to **enable 2-Step Verification** if not already enabled
   - You'll need to verify your phone number
   - This is required before you can create App Passwords

### Step 2: Generate an App Password

1. Go back to **Security** page
2. Under "How you sign in to Google", click **App passwords**
   - Direct link: https://myaccount.google.com/apppasswords
3. You may need to sign in again
4. Under "Select app", choose **Mail**
5. Under "Select device", choose **Other (Custom name)**
6. Type: `Online Banking System`
7. Click **Generate**
8. Google will show you a **16-character password** like: `abcd efgh ijkl mnop`
9. **Copy this password** (you won't see it again!)

### Step 3: Update email.properties

1. Open: `src/main/resources/email.properties`
2. Update with your details:

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.username=g2673769@gmail.com
mail.password=abcdefghijklmnop
```

**Important Notes:**
- Use the **16-character App Password** (without spaces)
- Use your **actual Gmail address** for `mail.username`
- Do NOT use your regular Gmail password

### Step 4: Restart Your Server

After updating `email.properties`:
1. Stop your Tomcat server
2. Restart it
3. Try sending an Email OTP again

## Example Configuration

```properties
# Gmail SMTP Configuration
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true

# Your Gmail credentials
mail.username=your-email@gmail.com
mail.password=your-16-char-app-password
```

## Testing

After configuration:
1. Register a new user or login
2. Choose **Email OTP** for MFA
3. Click **Send OTP**
4. Check your **Gmail inbox** for the OTP email
5. Also check the **console** - it still prints OTP there for backup

## Troubleshooting

### Still getting authentication error?
- Make sure 2-Step Verification is enabled
- Verify you're using the App Password, not your regular password
- Remove any spaces from the App Password
- Make sure the email address is correct

### Not receiving emails?
- Check your **Spam/Junk** folder
- Verify the email address is correct
- Check the console - OTP is still printed there as backup

### Want to use a different email service?
- **Outlook/Hotmail**: Use `smtp.office365.com` port `587`
- **Yahoo**: Use `smtp.mail.yahoo.com` port `587` (also needs App Password)
- **Custom SMTP**: Update host and port accordingly

## Security Warning

⚠️ **Never commit `email.properties` with real credentials to Git!**

Add to `.gitignore`:
```
src/main/resources/email.properties
```

## Alternative: Console-Only Mode

If you don't want to configure email, the system already prints OTPs to the console for testing. Just check the Tomcat console window for the OTP code!
