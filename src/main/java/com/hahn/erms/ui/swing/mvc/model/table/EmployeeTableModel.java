package com.hahn.erms.ui.swing.mvc.model.table;

import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmployeeTableModel extends AbstractTableModel {
    private final List<EmployeeModel> employees = new CopyOnWriteArrayList<>();
    private final String[] columnNames = {
            "ID", "Name", "Department", "Job Title", "Email", "Status", "Hire Date"
    };

    @Override
    public int getRowCount() {
        return employees.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EmployeeModel employee = employees.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> employee.getEmployeeId();
            case 1 -> employee.getFullName();
            case 2 -> employee.getDepartment();
            case 3 -> employee.getJobTitle();
            case 4 -> employee.getEmail();
            case 5 -> employee.getStatus();
            case 6 -> employee.getHireDate().toString();
            default -> null;
        };
    }

    public void setEmployees(List<EmployeeModel> employees) {
        this.employees.clear();
        if (employees != null) {
            this.employees.addAll(employees);
        }
        fireTableDataChanged();
    }

    public void addEmployee(EmployeeModel employee) {
        this.employees.add(employee);
        fireTableRowsInserted(employees.size() - 1, employees.size() - 1);
    }

    public void updateEmployee(int row, EmployeeModel employee) {
        this.employees.set(row, employee);
        fireTableRowsUpdated(row, row);
    }

    public void removeEmployee(int row) {
        this.employees.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public EmployeeModel getEmployeeAt(int row) {
        return employees.get(row);
    }

    public List<EmployeeModel> getAllEmployees() {
        return new ArrayList<>(employees);
    }
}