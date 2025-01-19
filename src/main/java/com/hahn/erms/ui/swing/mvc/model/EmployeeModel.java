package com.hahn.erms.ui.swing.mvc.model;

import com.hahn.erms.enums.EmploymentStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeModel {
    private Long id;
    private String employeeId;
    private String fullName;
    private String jobTitle;
    private String department;
    private LocalDate hireDate;
    private EmploymentStatus status;
    private String email;
    private String phone;
    private String address;


    public EmployeeModel(EmployeeModel other) {
        this.id = other.id;
        this.employeeId = other.employeeId;
        this.fullName = other.fullName;
        this.jobTitle = other.jobTitle;
        this.department = other.department;
        this.hireDate = other.hireDate;
        this.status = other.status;
        this.email = other.email;
        this.phone = other.phone;
        this.address = other.address;
    }

    public EmployeeModel() {}
}