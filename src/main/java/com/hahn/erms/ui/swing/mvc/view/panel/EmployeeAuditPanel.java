package com.hahn.erms.ui.swing.mvc.view.panel;

import com.hahn.erms.dto.EmployeeAuditDTO;
import com.hahn.erms.service.EmployeeAuditService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EmployeeAuditPanel extends JPanel {
    private final JTable auditTable;
    private final EmployeeAuditService auditService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EmployeeAuditPanel(EmployeeAuditService auditService) {
        this.auditService = auditService;
        setLayout(new BorderLayout());

        String[] columnNames = {
                "Timestamp", "Field", "Old Value", "New Value", "Modified By", "Type"
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table read-only
            }
        };

        auditTable = new JTable(model);
        auditTable.getTableHeader().setReorderingAllowed(false);
        auditTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        auditTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // Timestamp
        auditTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Field
        auditTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Old Value
        auditTable.getColumnModel().getColumn(3).setPreferredWidth(150);  // New Value
        auditTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Modified By
        auditTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Type

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(auditTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAuditHistory());
        buttonPanel.add(refreshButton);

        // Filter button
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> filterAuditHistory());
        buttonPanel.add(filterButton);

        add(buttonPanel, BorderLayout.SOUTH);

        auditService.getAllAuditHistory().forEach(audit -> {
            model.addRow(new Object[]{
                    audit.getRevisionTimestamp().format(dateFormatter),
                    audit.getFieldName(),
                    audit.getOldValue(),
                    audit.getNewValue(),
                    audit.getModifiedBy(),
                    audit.getRevisionType()
            });
        });
    }


    public void loadAuditHistory() {
        DefaultTableModel model = (DefaultTableModel) auditTable.getModel();
        model.setRowCount(0);  // Clear existing data

        try {
            List<EmployeeAuditDTO> auditHistory = auditService.getAllAuditHistory();
            log.info("Total audit entries found: {}", auditHistory.size());
            List<EmployeeAuditDTO> employeeAudits = auditHistory.stream().toList();


            for (EmployeeAuditDTO audit : employeeAudits) {
                model.addRow(new Object[]{
                        audit.getRevisionTimestamp().format(dateFormatter),
                        audit.getFieldName(),
                        audit.getOldValue(),
                        audit.getNewValue(),
                        audit.getModifiedBy(),
                        audit.getRevisionType()
                });
            }
        } catch (Exception e) {
            log.error("Error loading audit history", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading audit history",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void filterAuditHistory() {
        // Show input dialog to get filter criteria
        String[] options = {"Field Name", "Revision Type", "Modified By"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Filter By:",
                "Audit History Filter",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == -1) return;  // User cancelled

        String filterValue = JOptionPane.showInputDialog(
                this,
                "Enter " + options[choice] + " to filter:",
                "Filter Audit History",
                JOptionPane.PLAIN_MESSAGE
        );

        if (filterValue == null || filterValue.trim().isEmpty()) return;

        try {
            List<EmployeeAuditDTO> auditHistory = auditService.getAllAuditHistory();
            List<EmployeeAuditDTO> filteredHistory;

            switch (choice) {
                case 0:  // Field Name
                    filteredHistory = auditHistory.stream()
                            .filter(audit -> audit.getFieldName().toLowerCase().contains(filterValue.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                case 1:  // Revision Type
                    filteredHistory = auditHistory.stream()
                            .filter(audit -> audit.getRevisionType().toLowerCase().contains(filterValue.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                case 2:  // Modified By
                    filteredHistory = auditHistory.stream()
                            .filter(audit -> audit.getModifiedBy().toLowerCase().contains(filterValue.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                default:
                    return;
            }

            DefaultTableModel model = (DefaultTableModel) auditTable.getModel();
            model.setRowCount(0);
            populateAuditTable(filteredHistory, model);

        } catch (Exception e) {
            showErrorMessage("Error filtering audit history: " + e.getMessage());
        }
    }

    private void populateAuditTable(List<EmployeeAuditDTO> auditHistory, DefaultTableModel model) {
        for (EmployeeAuditDTO audit : auditHistory) {
            model.addRow(new Object[]{
                    audit.getRevisionTimestamp().format(dateFormatter),
                    audit.getFieldName(),
                    audit.getOldValue(),
                    audit.getNewValue(),
                    audit.getModifiedBy(),
                    audit.getRevisionType()
            });
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}