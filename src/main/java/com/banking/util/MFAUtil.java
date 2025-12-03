package com.banking.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MFAUtil {

    public static String generateTOTPSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String generateTOTPCode(String secret) {
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        return getTOTPCode(secret, timeIndex);
    }

    public static boolean validateTOTPCode(String secret, String code) {
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        // Check current, previous, and next intervals to account for clock drift
        for (int i = -1; i <= 1; i++) {
            String generatedCode = getTOTPCode(secret, timeIndex + i);
            if (generatedCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    private static String getTOTPCode(String secret, long timeIndex) {
        try {
            Base32 base32 = new Base32();
            byte[] secretBytes = base32.decode(secret);
            
            byte[] data = new byte[8];
            for (int i = 8; i-- > 0; timeIndex >>>= 8) {
                data[i] = (byte) timeIndex;
            }

            SecretKeySpec signKey = new SecretKeySpec(secretBytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;

            return String.format("%06d", truncatedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String generateQRCodeURL(String username, String secret) {
        try {
            String issuer = "OnlineBanking";
            return "https://quickchart.io/chart?chs=200x200&chld=M|0&cht=qr&chl=" +
                    URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                            issuer, username, secret, issuer), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String generateEmailOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
