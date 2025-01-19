package com.hahn.erms.ui.swing.mvc.view.panel;

import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.AuthenticationService;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.controller.LoginController;
import com.hahn.erms.ui.swing.mvc.view.MainFrame;
import net.miginfocom.swing.MigLayout;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Slf4j
public class LoginPanel extends JPanel {
    private final LoginController controller;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginPanel(MainFrame mainFrame, AuthenticationService authenticationService, EmployeeService employeeService, SecurityUtils securityUtils) {
        this.usernameField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
        this.loginButton = new JButton("Login");

        this.controller = new LoginController(this, authenticationService, mainFrame, employeeService, securityUtils);

        setupLayout();
        setupListeners();
    }

    private void setupLayout() {
        setLayout(new MigLayout("fillx, insets 20, wrap 2", "[][grow,fill]"));

        // Title
        JLabel titleLabel = new JLabel("EmployeeModel Records Management System");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        add(titleLabel, "span 2, center, gapbottom 20");

        // Login form
        add(new JLabel("Username:"), "right");
        add(usernameField, "growx");

        add(new JLabel("Password:"), "right");
        add(passwordField, "growx");

        // Login button
        add(loginButton, "span 2, center, gaptop 10");

        // Set minimum size
        setPreferredSize(new Dimension(400, 250));
    }

    private void setupListeners() {
        ActionListener loginActionListener = e -> handleLogin();

        loginButton.addActionListener(loginActionListener);
        passwordField.addActionListener(loginActionListener);

        // Set login button as default button for the panel
        registerKeyboardAction(loginActionListener,
                KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Username is required");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Password is required");
            passwordField.requestFocus();
            return;
        }

        controller.handleLogin(username, password);
    }

    public void clearFields() {
        usernameField.setText("");
        clearPasswordField();
        usernameField.requestFocus();
    }

    public void clearPasswordField() {
        passwordField.setText("");
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
    }
}