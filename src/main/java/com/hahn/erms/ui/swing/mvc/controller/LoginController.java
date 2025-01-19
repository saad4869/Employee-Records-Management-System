package com.hahn.erms.ui.swing.mvc.controller;

import com.hahn.erms.service.AuthenticationService;

import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.view.MainFrame;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeePanel;
import com.hahn.erms.ui.swing.mvc.view.panel.LoginPanel;
import org.springframework.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class LoginController {
    private final LoginPanel view;
    private final AuthenticationService authService;
    private final MainFrame mainFrame;
    private final EmployeeService employeeService;
    private final com.hahn.erms.security.SecurityUtils securityUtils;

    public LoginController(LoginPanel view,
                           AuthenticationService authService,
                           MainFrame mainFrame,
                           EmployeeService employeeService,
                           com.hahn.erms.security.SecurityUtils securityUtils) {
        this.view = view;
        this.authService = authService;
        this.mainFrame = mainFrame;
        this.employeeService = employeeService;
        this.securityUtils = securityUtils;
    }

    public void handleLogin(String username, String password) {
        try {
            log.debug("Attempting login for user: {}", username);
            Authentication auth = authService.authenticate(username, password);

            if (auth.isAuthenticated()) {
                log.info("User successfully authenticated: {}", username);

                // Add verification check
                Authentication checkAuth = SecurityContextHolder.getContext().getAuthentication();
                log.debug("Authentication after setAuthentication: {}", checkAuth);
                if (checkAuth != null) {
                    log.debug("Current authorities: {}", checkAuth.getAuthorities());
                }
                new EmployeePanel(employeeService,securityUtils);
                mainFrame.showMainScreen();
            } else {
                log.warn("Authentication failed for user: {}", username);
                view.showError("Invalid username or password");
                view.clearPasswordField();
            }
        } catch (Exception e) {
            log.error("Login error for user: " + username, e);
            view.showError("Login failed: " + e.getMessage());
            view.clearPasswordField();
        }
    }

    public void handleLogout() {
        try {
            authService.logout();
            mainFrame.showLoginScreen();
            view.clearFields();
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Logout error", e);
            view.showError("Logout failed: " + e.getMessage());
        }
    }
}