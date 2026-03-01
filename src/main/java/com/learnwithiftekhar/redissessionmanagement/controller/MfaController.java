package com.learnwithiftekhar.redissessionmanagement.controller;

import com.learnwithiftekhar.redissessionmanagement.dto.response.MfaSetupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/mfa")
@RequiredArgsConstructor
public class MfaController {

    @PostMapping("/setup")
    public ResponseEntity<MfaSetupResponse> setupMFA(Principal principal) {

        return null;
    }
}
