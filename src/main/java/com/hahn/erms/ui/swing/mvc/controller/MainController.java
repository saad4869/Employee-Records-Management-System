package com.hahn.erms.ui.swing.mvc.controller;

import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeAuditService;
import com.hahn.erms.service.AuthenticationService;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.view.MainFrame;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {
    private final AuthenticationService authService;
    private final EmployeeService employeeService;
    private final EmployeeAuditService auditService;
    private final SecurityUtils securityUtils;
    private MainFrame mainFrame;

    public MainController(AuthenticationService authService, EmployeeService employeeService, EmployeeAuditService auditService, SecurityUtils securityUtils) {
        this.authService = authService;
        this.employeeService = employeeService;
        this.auditService = auditService;
        this.securityUtils = securityUtils;
    }

    public void initialize() {
        try {
            log.info("Initializing EmployeeModel Records Management System");

            // Create and configure main frame
            mainFrame = new MainFrame(authService, employeeService,auditService,securityUtils);

            // Set up window close operation
            mainFrame.setDefaultCloseOperation(MainFrame.DO_NOTHING_ON_CLOSE);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    handleApplicationExit();
                }
            });

            // Display the main frame
            mainFrame.setVisible(true);

            log.info("Application UI initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize application", e);
            System.exit(1);
        }
    }

    private void handleApplicationExit() {
        try {
            // Perform cleanup operations
            if (authService.isAuthenticated()) {
                authService.logout();
            }

            // Close the application
            System.exit(0);
        } catch (Exception e) {
            log.error("Error during application shutdown", e);
            System.exit(1);
        }
    }
}