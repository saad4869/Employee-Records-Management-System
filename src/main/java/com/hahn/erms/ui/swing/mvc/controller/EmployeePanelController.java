package com.hahn.erms.ui.swing.mvc.controller;

import com.hahn.erms.security.SecurityAwareSwingWorker;
import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.hahn.erms.ui.swing.mvc.model.table.EmployeeTableModel;
import com.hahn.erms.ui.swing.mvc.view.dialog.EmployeeDialog;
import com.hahn.erms.ui.swing.mvc.view.panel.EmployeePanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Slf4j
public class EmployeePanelController {
    private final EmployeePanel view;
    private final EmployeeService employeeService;
    private final SecurityUtils securityUtils;
    @Getter
    private final EmployeeTableModel tableModel;
    private final Authentication authentication;

    public EmployeePanelController(EmployeePanel view, EmployeeService employeeService, SecurityUtils securityUtils) {
        this.view = view;
        this.employeeService = employeeService;
        this.securityUtils = securityUtils;
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        this.tableModel = new EmployeeTableModel();


        // Initial setup based on user role
        if (securityUtils.hasRole("ROLE_MANAGER") &&
                !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            // Manager can only see their department
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            view.disableDepartmentSelection();
            view.setDepartment(managerDepartment);
            log.info("department is "+managerDepartment);
        }

        // Load initial data
        loadEmployees();
    }

    public void loadEmployees() {
        // Determine department based on user role
        String department = null;
        if (securityUtils.hasRole("ROLE_MANAGER") &&
                !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            department = securityUtils.getCurrentUserDepartment();
        }

        final String finalDepartment = department;
        new SecurityAwareSwingWorker<List<EmployeeModel>, Void>() {
            @Override
            protected List<EmployeeModel> doSecuredWork() {
                Page<EmployeeModel> page = employeeService.findAll(finalDepartment, Pageable.unpaged());
                return page.getContent();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setEmployees(get());
                } catch (Exception e) {
                    log.error("Error loading employees", e);
                    view.showError("Error loading employees: " + e.getMessage());
                }
            }
        }.execute();
    }
    public void handleSearch(String query, String department) {
        // If manager, department is handled in service layer
        new SecurityAwareSwingWorker<List<EmployeeModel>, Void>() {
            @Override
            protected List<EmployeeModel> doSecuredWork() {
                Page<EmployeeModel> page;
                if (query != null && !query.isEmpty()) {
                    page = employeeService.search(query, Pageable.unpaged());
                } else {
                    // When no search query, use findAll with department filter
                    page = employeeService.findAll(department, Pageable.unpaged());
                }
                return page.getContent();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setEmployees(get());
                } catch (Exception e) {
                    log.error("Error searching employees", e);
                    view.showError("Error searching employees: " + e.getMessage());
                }
            }
        }.execute();
    }

    public void handleAdd() {
        EmployeeDialog dialog = new EmployeeDialog(view.getParentWindow(), null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            EmployeeModel employee = dialog.getEmployee();

            new SecurityAwareSwingWorker<EmployeeModel, Void>() {
                @Override
                protected EmployeeModel doSecuredWork() throws Exception {
                    return employeeService.create(employee);
                }

                @Override
                protected void done() {
                    try {
                        EmployeeModel savedEmployee = get();
                        tableModel.addEmployee(savedEmployee);
                        view.showSuccess("Employee created successfully");
                    } catch (Exception e) {
                        log.error("Error creating employee", e);
                        String errorMessage = e.getCause() != null ?
                                e.getCause().getMessage() : e.getMessage();
                        view.showError("Error creating employee: " + errorMessage);
                    }
                }
            }.execute();
        }
    }

    public void handleEdit(int selectedRow) {
        if (selectedRow >= 0) {
            EmployeeModel employee = tableModel.getEmployeeAt(selectedRow);
            EmployeeDialog dialog = new EmployeeDialog(view.getParentWindow(), employee);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                EmployeeModel updatedEmployee = dialog.getEmployee();
                new SecurityAwareSwingWorker<EmployeeModel, Void>() {
                    @Override
                    protected EmployeeModel doSecuredWork() {
                        return employeeService.update(employee.getId(), updatedEmployee);
                    }

                    @Override
                    protected void done() {
                        try {
                            tableModel.updateEmployee(selectedRow, get());
                            view.showSuccess("Employee updated successfully");
                        } catch (Exception e) {
                            log.error("Error updating employee", e);
                            view.showError("Error updating employee: " + e.getMessage());
                        }
                    }
                }.execute();
            }
        }
    }

    public void handleDelete(int selectedRow) {
        if (selectedRow >= 0) {
            EmployeeModel employee = tableModel.getEmployeeAt(selectedRow);

            if (view.confirmDelete()) {
                new SecurityAwareSwingWorker<Void, Void>() {
                    @Override
                    protected Void doSecuredWork() {
                        employeeService.delete(employee.getId());
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            tableModel.removeEmployee(selectedRow);
                            view.showSuccess("Employee deleted successfully");
                        } catch (Exception e) {
                            log.error("Error deleting employee", e);
                            view.showError("Error deleting employee: " + e.getMessage());
                        }
                    }
                }.execute();
            }
        }
    }
}