package com.hahn.erms.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class AuthResponse {
    private String username;
    private String roles;
}