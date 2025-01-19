package com.hahn.erms.ui.swing.mvc.view.dialog;

import com.hahn.erms.ui.swing.mvc.controller.EmployeeDialogController;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeeFormPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class EmployeeDialog extends JDialog {
    private final EmployeeDialogController controller;
    private final EmployeeFormPanel formPanel;
    @Getter
    @Setter
    private boolean confirmed = false;
    @Setter
    private EmployeeModel resultEmployee;

    public EmployeeDialog(Window owner, EmployeeModel employee) {
        super(owner, employee == null ? "Add EmployeeModel" : "Edit EmployeeModel", ModalityType.APPLICATION_MODAL);

        this.formPanel = new EmployeeFormPanel();
        this.controller = new EmployeeDialogController(this, formPanel);

        if (employee != null) {
            this.resultEmployee = new EmployeeModel(employee); // Create a copy
            controller.setEmployee(employee);
        }

        setupLayout();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Add form panel
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(formPanel, BorderLayout.CENTER);

        // Add button panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> controller.handleOk());
        cancelButton.addActionListener(e -> controller.handleCancel());

        // Set OK button as default button
        getRootPane().setDefaultButton(okButton);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public EmployeeModel getEmployee() {
        return resultEmployee;
    }

    // This method is called by the controller when validation succeeds
    public void acceptAndClose() {
        setVisible(false);
        dispose();
    }
}