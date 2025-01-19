package com.hahn.erms.controller;

import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.hahn.erms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Page<EmployeeModel> findAll(
            @RequestParam(required = false) String department,
            Pageable pageable) {
        log.debug("REST request to get all Employees, department: {}", department);
        return employeeService.findAll(department, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeModel> findById(@PathVariable Long id) {
        log.debug("REST request to get Employee: {}", id);
        EmployeeModel employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_PERSONNEL')")
    public ResponseEntity<EmployeeModel> create(@Valid @RequestBody EmployeeModel employee) {
        log.debug("REST request to create Employee: {}", employee);
        if (employee.getId() != null) {
            throw new IllegalArgumentException("A new employee cannot already have an ID");
        }
        EmployeeModel result = employeeService.create(employee);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_PERSONNEL', 'MANAGER')")
    public ResponseEntity<EmployeeModel> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeModel employee) {
        log.debug("REST request to update Employee: {}", id);
        if (employee.getId() == null) {
            throw new IllegalArgumentException("Invalid id");
        }
        if (!id.equals(employee.getId())) {
            throw new IllegalArgumentException("Invalid id");
        }
        EmployeeModel result = employeeService.update(id, employee);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_PERSONNEL')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Employee: {}", id);
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<EmployeeModel> search(
            @RequestParam String query,
            Pageable pageable) {
        log.debug("REST request to search Employees: {}", query);
        return employeeService.search(query, pageable);
    }

    @GetMapping("/exists/{employeeId}")
    public ResponseEntity<Boolean> existsByEmployeeId(@PathVariable String employeeId) {
        log.debug("REST request to check if Employee exists: {}", employeeId);
        return ResponseEntity.ok(employeeService.existsByEmployeeId(employeeId));
    }
}