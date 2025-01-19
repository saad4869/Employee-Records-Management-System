package com.hahn.erms.ui.swing.mvc.view.panel;

import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.controller.EmployeePanelController;
import net.miginfocom.swing.MigLayout;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

@Slf4j
public class EmployeePanel extends JPanel {
    private final EmployeePanelController controller;

    private JTable employeeTable;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    private JComboBox<String> departmentComboBox;

    public void disableDepartmentSelection() {
        departmentComboBox.setEnabled(false);
    }

    public void setDepartment(String department) {
        departmentComboBox.setSelectedItem(department);
    }

    public EmployeePanel(EmployeeService employeeService, SecurityUtils securityUtils) {
        this.controller = new EmployeePanelController(this, employeeService,securityUtils);

        setupLayout();
        initializeComponents();
        setupListeners();


        controller.loadEmployees();
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search panel
        JPanel searchPanel = new JPanel(new MigLayout("insets 5", "[][grow][]"));
        searchField = new JTextField(20);
        departmentFilter = new JComboBox<>(new String[]{"All Departments", "IT", "HR", "Finance"});

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField, "growx");
        searchPanel.add(departmentFilter);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchPanel, gbc);

        // Table
        employeeTable = new JTable(controller.getTableModel());
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoCreateRowSorter(true);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(employeeTable), gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new MigLayout("insets 5", "[grow][][][][grow]"));
        addButton = new JButton("Add Employee");
        editButton = new JButton("Edit Employee");
        deleteButton = new JButton("Delete Employee");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonPanel, gbc);
    }

    private void initializeComponents() {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Configure table properties
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.setRowHeight(25);
        employeeTable.setFillsViewportHeight(true);
    }

    private void setupListeners() {
        // Search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        departmentFilter.addActionListener(e -> performSearch());

        // Button listeners
        addButton.addActionListener(e -> controller.handleAdd());
        editButton.addActionListener(e -> controller.handleEdit(employeeTable.getSelectedRow()));
        deleteButton.addActionListener(e -> controller.handleDelete(employeeTable.getSelectedRow()));

        // Table selection listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = employeeTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        String department = Objects.requireNonNull(departmentFilter.getSelectedItem()).toString();
        controller.handleSearch(searchText, department);
    }

    // Utility methods for controllers
    public Window getParentWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}