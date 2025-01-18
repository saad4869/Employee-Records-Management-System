package com.hahn.erms.controller;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Page<Employee> findAll(
            @RequestParam(required = false) String department,
            Pageable pageable) {
        return employeeService.findAll(department, pageable);
    }

    @GetMapping("/{id}")
    public Employee findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_PERSONNEL')")
    public Employee create(@Valid @RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_PERSONNEL', 'MANAGER')")
    public Employee update(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_PERSONNEL')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<Employee> search(
            @RequestParam String query,
            Pageable pageable) {
        return employeeService.search(query, pageable);
    }
}