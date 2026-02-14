package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.configuration.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class JwtController {
    private final JwtUtils jwtUtils;

    @GetMapping("/api/auth/{token}")
    public ResponseEntity<?> isAuthTokenCorrect(@PathVariable String token){
        boolean jwtIsCorrect = jwtUtils.validateJwt(token);
        return ResponseEntity.ok(jwtIsCorrect);
    }
}
