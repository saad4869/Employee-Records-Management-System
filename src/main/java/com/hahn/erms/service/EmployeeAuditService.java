package com.hahn.erms.service;

import com.hahn.erms.dto.EmployeeAuditDTO;
import com.hahn.erms.entity.Employee;
import com.hahn.erms.entity.CustomRevisionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeAuditService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<EmployeeAuditDTO> getAllAuditHistory() {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        List<Object[]> revisions = auditReader.createQuery()
                .forRevisionsOfEntity(Employee.class, false, true)
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        System.out.println("Total revisions found: " + revisions.size());

        List<EmployeeAuditDTO> auditDTOs = new ArrayList<>();
        Employee previousVersion = null;
        Long previousEmployeeId = null;

        for (Object[] revision : revisions) {
            Employee currentVersion = (Employee) revision[0];
            CustomRevisionEntity revEntity = (CustomRevisionEntity) revision[1];
            RevisionType revType = (RevisionType) revision[2];


            // Check if we're dealing with a new employee
            if (previousEmployeeId == null || !currentVersion.getId().equals(previousEmployeeId)) {
                previousVersion = null;
            }

            if (previousVersion != null) {
                compareVersions(previousVersion, currentVersion, revEntity, revType, auditDTOs);
            } else if (revType == RevisionType.ADD) {
                // Handle initial creation
                createInitialAuditEntry(currentVersion, revEntity, auditDTOs);
            }

            previousVersion = currentVersion;
            previousEmployeeId = currentVersion.getId();
        }

        System.out.println("Total audit DTOs created: " + auditDTOs.size());
        return auditDTOs;
    }

    private void compareVersions(Employee oldVersion, Employee newVersion,
                                 CustomRevisionEntity revEntity, RevisionType revType,
                                 List<EmployeeAuditDTO> auditDTOs) {
        // Compare each field and create audit entries for changes
        compareField("Full Name", oldVersion.getFullName(), newVersion.getFullName(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Job Title", oldVersion.getJobTitle(), newVersion.getJobTitle(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Department", oldVersion.getDepartment(), newVersion.getDepartment(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Email", oldVersion.getEmail(), newVersion.getEmail(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Phone", oldVersion.getPhone(), newVersion.getPhone(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Address", oldVersion.getAddress(), newVersion.getAddress(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
        compareField("Status", oldVersion.getStatus().name(), newVersion.getStatus().name(),
                revEntity, revType, oldVersion.getId(), auditDTOs);
    }

    private void compareField(String fieldName, String oldValue, String newValue,
                              CustomRevisionEntity revEntity, RevisionType revType,
                              Long entityId, List<EmployeeAuditDTO> auditDTOs) {
        if ((oldValue != null && !oldValue.equals(newValue)) ||
                (oldValue == null && newValue != null)) {
            EmployeeAuditDTO dto = new EmployeeAuditDTO();
            dto.setId(entityId);
            dto.setRevisionNumber(revEntity.getId());
            dto.setRevisionTimestamp(LocalDateTime.ofInstant(
                    java.util.Date.from(revEntity.getRevisionDate().toInstant()).toInstant(),
                    java.time.ZoneId.systemDefault()
            ));
            dto.setRevisionType(revType.name());
            dto.setModifiedBy(revEntity.getUsername());
            dto.setFieldName(fieldName);
            dto.setOldValue(oldValue);
            dto.setNewValue(newValue);
            auditDTOs.add(dto);
        }
    }

    private void createInitialAuditEntry(Employee employee, CustomRevisionEntity revEntity,
                                         List<EmployeeAuditDTO> auditDTOs) {
        EmployeeAuditDTO dto = new EmployeeAuditDTO();
        dto.setId(employee.getId());
        dto.setRevisionNumber(revEntity.getId());
        dto.setRevisionTimestamp(LocalDateTime.ofInstant(
                java.util.Date.from(revEntity.getRevisionDate().toInstant()).toInstant(),
                java.time.ZoneId.systemDefault()
        ));
        dto.setRevisionType("CREATE");
        dto.setModifiedBy(revEntity.getUsername());
        dto.setFieldName("Initial Creation");
        dto.setOldValue(null);
        dto.setNewValue("Employee Created");
        auditDTOs.add(dto);
    }
}