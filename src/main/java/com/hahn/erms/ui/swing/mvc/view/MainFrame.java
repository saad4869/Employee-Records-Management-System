package com.hahn.erms.ui.swing.mvc.view;

import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeAuditService;
import com.hahn.erms.service.AuthenticationService;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeeAuditPanel;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeePanel;
import com.hahn.erms.ui.swing.mvc.view.panel.LoginPanel;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.naming.AuthenticationException;
import javax.swing.*;
import java.awt.*;

@Slf4j
@Component
public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final LoginPanel loginPanel;
    private final JTabbedPane tabbedPane;
    private final AuthenticationService authenticationService;
    private final EmployeeAuditService auditService;
    private final SecurityUtils securityUtils;
    private JDialog auditDialog;
    private JPanel headerPanel;
    private JButton auditButton;

    public MainFrame(
            AuthenticationService authenticationService,
            EmployeeService employeeService,
            EmployeeAuditService auditService,
            SecurityUtils securityUtils) {

        this.authenticationService = authenticationService;
        this.auditService = auditService;
        this.securityUtils = securityUtils;

        setTitle("Employee Records Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize layout managers
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.tabbedPane = new JTabbedPane();

        // Initialize panels
        this.loginPanel = new LoginPanel(this, authenticationService,employeeService,securityUtils);

        // Create main content panel with header and tabs
        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));

        // Create header panel with logout button
        this.headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, "growx, wrap");

        // Add employee panel to tabs
        EmployeePanel employeePanel = new EmployeePanel(employeeService,securityUtils);
        tabbedPane.addTab("Employees", employeePanel);

        contentPanel.add(tabbedPane, "grow");

        // Setup main panel with card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(contentPanel, "MAIN");

        // Add to frame
        getContentPane().add(mainPanel);

        // Configure frame
        setupFrame();

        // Create audit dialog
        createAuditDialog();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("insets 5", "[grow][][]"));

        // Add system title
        JLabel titleLabel = new JLabel("Employee Records Management System");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        headerPanel.add(titleLabel, "grow");

        // Create (but don't add yet) audit button
        auditButton = new JButton("Audit Logs");
        auditButton.addActionListener(e -> showAuditDialog());
        auditButton.setVisible(false);  // Hidden by default

        // Add logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            try {
                handleLogout();
            } catch (AuthenticationException ex) {
                throw new RuntimeException(ex);
            }
        });

        headerPanel.add(auditButton);
        headerPanel.add(logoutButton);

        // Add a separator below the header
        JSeparator separator = new JSeparator();
        headerPanel.add(separator, "newline, span, growx");

        return headerPanel;
    }

    private void updateHeaderPanel() {
        if (securityUtils.hasRole("ROLE_ADMINISTRATOR")) {
            auditButton.setVisible(true);
        } else {
            auditButton.setVisible(false);
        }
        headerPanel.revalidate();
        headerPanel.repaint();
    }

    private void createAuditDialog() {
        auditDialog = new JDialog(this, "Audit Logs", false);
        auditDialog.setLayout(new BorderLayout());

        // Create audit panel
        EmployeeAuditPanel auditPanel = new EmployeeAuditPanel(auditService);
        auditDialog.add(auditPanel, BorderLayout.CENTER);

        // Add close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> auditDialog.dispose());
        buttonPanel.add(closeButton);
        auditDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        auditDialog.setSize(900, 600);
        auditDialog.setLocationRelativeTo(this);
    }

    private void showAuditDialog() {
        if (auditDialog != null) {
            auditDialog.setVisible(true);
        }
    }

    private void handleLogout() throws AuthenticationException {
        log.debug("User logged out");
        if (auditDialog != null) {
            auditDialog.dispose();
        }
        auditButton.setVisible(false);
        authenticationService.logout();
        showLoginScreen();
    }

    private void setupFrame() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            log.error("Failed to set system look and feel", e);
        }

        // Set frame properties
        setPreferredSize(new Dimension(1024, 768));
        pack();
        setLocationRelativeTo(null);

        // Show login screen initially
        showLoginScreen();
    }

    public void showLoginScreen() {
        log.debug("Showing login screen");
        cardLayout.show(mainPanel, "LOGIN");
        loginPanel.clearFields();
    }

    public void showMainScreen() {
        log.debug("Showing main screen");
        cardLayout.show(mainPanel, "MAIN");
        updateHeaderPanel();  // Update header after successful login
    }
}