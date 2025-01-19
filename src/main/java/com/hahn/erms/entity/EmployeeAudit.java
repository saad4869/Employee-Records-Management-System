package com.hahn.erms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLOYEE_AUDIT_LOG")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class EmployeeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(
            name = "audit_seq",
            sequenceName = "EMPLOYEE_AUDIT_LOG_SEQ",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(name = "old_value")
    @Lob
    private String oldValue;

    @Column(name = "new_value")
    @Lob
    private String newValue;

    @CreatedBy
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @CreatedDate
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;
}