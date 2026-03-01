package com.learnwithiftekhar.redissessionmanagement.service;

import com.learnwithiftekhar.redissessionmanagement.dto.response.MfaSetupResponse;
import com.learnwithiftekhar.redissessionmanagement.repository.UserRepository;
import com.learnwithiftekhar.redissessionmanagement.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MfaService {
    private final UserRepository userRepository;
    private final TotpUtil totpUtil;

    // 1. MFA setup
    // 2. First time verification to confirm MFA
    // 3. MFA verification for login

    public MfaSetupResponse setupMfa(String username) {
        // query the database to check it the user exist

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        // 1. Check if maf is already enabled
        if(user.isMfaEnabled()){
            throw new IllegalStateException("User is already mfa enabled");
        }

        // 2.Generate the secret and save this (not yet enabled)
        String secret = totpUtil.generateSecret();
        // TODO: Implement secret key encryption
        user.setMfaSecret(secret);
        userRepository.save(user);

        String otpAuthUri = totpUtil.buildOtpAuthUrl(username,secret);
        String qrCode = totpUtil.generateQRCodeBase64(otpAuthUri);

        return MfaSetupResponse.builder()
                .secret(secret)
                .qrCodeUri(otpAuthUri)
                .qrCodeImage(qrCode)
                .build();
    }
}
