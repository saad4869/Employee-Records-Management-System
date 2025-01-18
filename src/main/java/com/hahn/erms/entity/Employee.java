package com.hahn.erms.entity;

import com.hahn.erms.enums.EmploymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLOYEE")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(
            name = "employee_seq",
            sequenceName = "EMPLOYEE_SEQ",
            allocationSize = 1
    )
    private Long id;

    @NotBlank(message = "Employee ID is required")
    @Column(name = "EMPLOYEE_ID", unique = true, nullable = false)
    private String employeeId;

    @NotBlank(message = "Full name is required")
    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @NotBlank(message = "Job title is required")
    @Column(name = "JOB_TITLE", nullable = false)
    private String jobTitle;

    @NotBlank(message = "Department is required")
    @Column(name = "DEPARTMENT", nullable = false)
    private String department;

    @Past(message = "Hire date must be in the past")
    @Column(name = "HIRE_DATE", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMPLOYMENT_STATUS", nullable = false)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;


    @Email(message = "Invalid email format")
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    // One-to-One relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "CREATED_BY")

    private String createdBy;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    private String updatedBy;
}