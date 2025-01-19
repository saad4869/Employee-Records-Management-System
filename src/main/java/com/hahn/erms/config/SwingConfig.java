package com.hahn.erms.config;

import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeAuditService;
import com.hahn.erms.service.AuthenticationService;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.view.MainFrame;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeePanel;
import com.hahn.erms.ui.swing.mvc.view.panel.LoginPanel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwingConfig {

    @Bean
    public EmployeePanel employeePanel(EmployeeService employeeService,SecurityUtils securityUtils) {
        return new EmployeePanel(employeeService,securityUtils);
    }

    @Bean
    public LoginPanel loginPanel(MainFrame mainFrame, AuthenticationService authenticationService, EmployeeService employeeService, SecurityUtils securityUtils) {
        return new LoginPanel(mainFrame, authenticationService, employeeService, securityUtils);
    }

    @Bean
    public MainFrame mainFrame(
            AuthenticationService authenticationService,
            EmployeeService employeeService,
            EmployeeAuditService auditService,
            SecurityUtils securityUtils) {
        return new MainFrame(authenticationService, employeeService, auditService, securityUtils);
    }
}