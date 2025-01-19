package com.hahn.erms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAuditDTO {
    private Long id;
    private Integer revisionNumber;
    private LocalDateTime revisionTimestamp;
    private String revisionType;
    private String modifiedBy;
    private String fieldName;
    private String oldValue;
    private String newValue;

    @Override
    public String toString() {
        return String.format("AuditDTO[id=%d, field=%s, oldValue=%s, newValue=%s, by=%s, at=%s]",
                id, fieldName, oldValue, newValue, modifiedBy, revisionTimestamp);
    }
}