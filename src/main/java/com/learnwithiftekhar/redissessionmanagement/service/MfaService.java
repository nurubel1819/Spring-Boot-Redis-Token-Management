package com.learnwithiftekhar.redissessionmanagement.service;

import com.learnwithiftekhar.redissessionmanagement.dto.response.MfaSetupResponse;
import com.learnwithiftekhar.redissessionmanagement.model.User;
import com.learnwithiftekhar.redissessionmanagement.repository.UserRepository;
import com.learnwithiftekhar.redissessionmanagement.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MfaService {
    private final UserRepository userRepository;
    private final TotpUtil totpUtil;
    private final EncryptionService encryptionService;

    // 1. MFA setup
    // 2. First time verification to confirm MFA
    // 3. MFA verification for login

    public MfaSetupResponse setupMfa(String username) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // query the database to check it the user exist

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        // 1. Check if maf is already enabled
        if(user.isMfaEnabled()){
            throw new IllegalStateException("User is already mfa enabled");
        }

        // 2.Generate the secret and save this (not yet enabled)
        String rawSecret = totpUtil.generateSecret();
        String encryptedSecret = encryptionService.encrypt(rawSecret);

        user.setMfaSecret(encryptedSecret);
        userRepository.save(user);

        String otpAuthUri = totpUtil.buildOtpAuthUrl(username,rawSecret);
        String qrCode = totpUtil.generateQRCodeBase64(otpAuthUri);

        return MfaSetupResponse.builder()
                .secret(rawSecret)
                .qrCodeUri(otpAuthUri)
                .qrCodeImage(qrCode)
                .build();
    }
}
