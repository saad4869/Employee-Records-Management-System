package com.hahn.erms.ui.swing.mvc.view.panel;

import com.hahn.erms.enums.EmploymentStatus;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.toedter.calendar.JDateChooser;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EmployeeFormPanel extends JPanel {
    // Getters for form validation
    @Getter
    private final JTextField employeeIdField;
    @Getter
    private final JTextField fullNameField;
    @Getter
    private final JTextField jobTitleField;
    private final JComboBox<String> departmentCombo;
    @Getter
    private final JDateChooser hireDateField;
    private final JComboBox<EmploymentStatus> statusCombo;
    @Getter
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField addressField;

    public EmployeeFormPanel() {
        setLayout(new MigLayout("wrap 2, fillx", "[][grow,fill]", "[]10[]"));

        // Initialize components
        employeeIdField = new JTextField(20);
        fullNameField = new JTextField(20);
        jobTitleField = new JTextField(20);
        departmentCombo = new JComboBox<>(new String[]{"IT", "HR", "Finance"});
        hireDateField = new JDateChooser();
        hireDateField.setDateFormatString("yyyy-MM-dd");
        statusCombo = new JComboBox<>(EmploymentStatus.values());
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        addressField = new JTextField(20);

        setupComponents();
    }

    private void setupComponents() {
        // Required fields marked with *
        add(new JLabel("EmployeeModel ID: *"));
        add(employeeIdField);

        add(new JLabel("Full Name: *"));
        add(fullNameField);

        add(new JLabel("Job Title: *"));
        add(jobTitleField);

        add(new JLabel("Department: *"));
        add(departmentCombo);

        add(new JLabel("Hire Date: * (YYYY-MM-DD)"));
        add(hireDateField);

        add(new JLabel("Status: *"));
        add(statusCombo);

        add(new JLabel("Email:"));
        add(emailField);

        add(new JLabel("Phone:"));
        add(phoneField);

        add(new JLabel("Address:"));
        add(addressField);
    }

    public EmployeeModel getEmployeeData() {
        EmployeeModel employee = new EmployeeModel();
        employee.setEmployeeId(employeeIdField.getText().trim());
        employee.setFullName(fullNameField.getText().trim());
        employee.setJobTitle(jobTitleField.getText().trim());
        employee.setDepartment(departmentCombo.getSelectedItem().toString());
        Date hireDate = hireDateField.getDate();
        if (hireDate != null) {
            employee.setHireDate(hireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }        employee.setStatus((EmploymentStatus) statusCombo.getSelectedItem());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setAddress(addressField.getText().trim());
        return employee;
    }

    public void setEmployeeData(EmployeeModel employee) {
        if (employee != null) {
            employeeIdField.setText(employee.getEmployeeId());
            fullNameField.setText(employee.getFullName());
            jobTitleField.setText(employee.getJobTitle());
            departmentCombo.setSelectedItem(employee.getDepartment());
            hireDateField.setDate(java.sql.Date.valueOf(employee.getHireDate()));
            statusCombo.setSelectedItem(employee.getStatus());
            emailField.setText(employee.getEmail());
            phoneField.setText(employee.getPhone());
            addressField.setText(employee.getAddress());
        }
    }



    public void clearForm() {
        employeeIdField.setText("");
        fullNameField.setText("");
        jobTitleField.setText("");
        departmentCombo.setSelectedIndex(0);
        hireDateField.setDate(java.sql.Date.valueOf(LocalDate.now()));
        statusCombo.setSelectedItem(EmploymentStatus.ACTIVE);
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

}