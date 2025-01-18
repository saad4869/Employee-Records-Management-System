package com.hahn.erms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", auth.getName());
            response.put("roles", auth.getAuthorities());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }
}