package com.hahn.erms.ui.swing.mvc.controller;

import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.hahn.erms.ui.swing.mvc.view.dialog.EmployeeDialog;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeeFormPanel;

import javax.swing.JTextField;
import java.util.Calendar;

public class EmployeeDialogController {
    private final EmployeeDialog view;
    private final EmployeeFormPanel formPanel;

    public EmployeeDialogController(EmployeeDialog view, EmployeeFormPanel formPanel) {
        this.view = view;
        this.formPanel = formPanel;
    }

    public void setEmployee(EmployeeModel employee) {
        formPanel.setEmployeeData(employee);
    }

    public void handleOk() {
        try {
            validateFields();
            EmployeeModel employee = formPanel.getEmployeeData();
            view.setResultEmployee(employee);
            view.setConfirmed(true);
            view.acceptAndClose();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleCancel() {
        view.setConfirmed(false);
        view.dispose();
    }

    private void validateFields() throws Exception {
        // Validate required fields
        validateRequiredField(formPanel.getEmployeeIdField(), "EmployeeModel ID");
        validateRequiredField(formPanel.getFullNameField(), "Full Name");
        validateRequiredField(formPanel.getJobTitleField(), "Job Title");
        validateHireDate();
        validateEmail();
    }

    private void validateRequiredField(JTextField field, String fieldName) throws Exception {
        if (field.getText().trim().isEmpty()) {
            throw new Exception(fieldName + " is required");
        }
    }

    private void validateHireDate() throws Exception {
        if (formPanel.getHireDateField().getDate() == null) {
            throw new Exception("Hire date is required");
        }

        Calendar hireCalendar = Calendar.getInstance();
        hireCalendar.setTime(formPanel.getHireDateField().getDate());

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (hireCalendar.after(today)) {
            throw new Exception("Hire date cannot be in the future");
        }
    }

    private void validateEmail() throws Exception {
        String email = formPanel.getEmailField().getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Invalid email format");
        }
    }
}