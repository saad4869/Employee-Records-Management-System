package com.hahn.erms.service;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.entity.User;
import com.hahn.erms.enums.EmploymentStatus;
import com.hahn.erms.repository.EmployeeRepository;
import com.hahn.erms.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Employee> findAll(String department, Pageable pageable) {
        if (department != null && !department.isEmpty()) {
            return employeeRepository.findByDepartment(department, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Transactional
    public Employee create(@Valid Employee employee) {
        try {
            // Check if employeeId already exists
            if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
                throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
            }

            // Set default status if not set
            if (employee.getStatus() == null) {
                employee.setStatus(EmploymentStatus.ACTIVE);
            }

            // Save employee
            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Created employee with ID: {}", savedEmployee.getId());
            return savedEmployee;
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating employee: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Employee update(Long id, Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if new employee ID is unique (if changed)
        if (!existing.getEmployeeId().equals(employee.getEmployeeId()) &&
                employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
        }

        // Update fields but preserve relationships
        existing.setEmployeeId(employee.getEmployeeId());
        existing.setFullName(employee.getFullName());
        existing.setJobTitle(employee.getJobTitle());
        existing.setDepartment(employee.getDepartment());
        existing.setHireDate(employee.getHireDate());
        existing.setStatus(employee.getStatus());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setAddress(employee.getAddress());

        return employeeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Handle user relationship if exists
        if (employee.getUser() != null) {
            User user = employee.getUser();
            user.setEmployee(null);
            employee.setUser(null);
            userRepository.save(user);
        }

        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public Page<Employee> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return employeeRepository.findAll(pageable);
        }
        return employeeRepository.search(query.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> findByDepartment(String department, Pageable pageable) {
        return employeeRepository.findByDepartment(department, pageable);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(String employeeId) {
        return employeeRepository.existsByEmployeeId(employeeId);
    }
}