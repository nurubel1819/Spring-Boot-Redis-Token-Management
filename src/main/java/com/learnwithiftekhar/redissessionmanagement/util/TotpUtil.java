package com.learnwithiftekhar.redissessionmanagement.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TotpUtil {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Value("${mfa.app.name}")
    private String appName;


    // 1. Generating the secret key
    public String generateSecret() {
        return gAuth.createCredentials().getKey();
    }
    // 2. Build the otpAuth url
    public String buildOtpAuthUrl(String username, String secret) {
        //formate = otpauth://<NAME_OF_THE_APP>:<USER_NAME>?secret
        return String.format(
                "otpauth://totp/%s:%s?secret=%s",
                URLEncoder.encode(appName, StandardCharsets.UTF_8),
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                URLEncoder.encode(secret, StandardCharsets.UTF_8)
                );
    }
    // 3. Generation the QR Code
    public String generateQRCodeBase64(String otpAuthUri){
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(
                    otpAuthUri,
                    BarcodeFormat.QR_CODE,
                    200,
                    200
            );

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    pngOutputStream
            );

            return "data:image/png;base64," + Base64.getEncoder()
                    .encodeToString(pngOutputStream.toByteArray());
        }catch (Exception e){
            throw new RuntimeException("Could not generate QR Code",e);
        }
    }
    // 4. OTP code verification
    public boolean verifyCode(String secret, String codeStr){
        int code = Integer.parseInt(codeStr);
        return gAuth.authorize(secret, code);
    }
}
