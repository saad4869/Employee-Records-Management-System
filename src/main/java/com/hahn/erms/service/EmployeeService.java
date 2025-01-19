package com.hahn.erms.service;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.entity.User;
import com.hahn.erms.enums.EmploymentStatus;
import com.hahn.erms.mapper.EmployeeMapper;
import com.hahn.erms.repository.EmployeeRepository;
import com.hahn.erms.repository.UserRepository;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import com.hahn.erms.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<EmployeeModel> findAll(String department, Pageable pageable) {
        Page<Employee> employeePage;

        if (securityUtils.hasRole("ROLE_MANAGER") && !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            // Managers can only see employees in their department
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            employeePage = employeeRepository.findByDepartment(managerDepartment, pageable);
        } else if (department != null && !department.isEmpty()) {
            employeePage = employeeRepository.findByDepartment(department, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        return employeeMapper.toModelPage(employeePage);
    }

    @Transactional(readOnly = true)
    public EmployeeModel findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (securityUtils.hasRole("ROLE_MANAGER") && !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            if (!employee.getDepartment().equals(managerDepartment)) {
                throw new AccessDeniedException("Access denied: Cannot view employee from different department");
            }
        }

        return employeeMapper.toModel(employee);
    }

    @Transactional
    public EmployeeModel create(@Valid EmployeeModel employeeModel) {
        if (!securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            throw new AccessDeniedException("Access denied: Only HR Personnel and Admins can create employees");
        }

        try {
            if (employeeRepository.existsByEmployeeId(employeeModel.getEmployeeId())) {
                throw new RuntimeException("Employee ID already exists: " + employeeModel.getEmployeeId());
            }

            Employee employee = employeeMapper.toEntity(employeeModel);

            if (employee.getStatus() == null) {
                employee.setStatus(EmploymentStatus.ACTIVE);
            }

            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Created employee with ID: {}", savedEmployee.getId());

            // Log the creation in audit
            return employeeMapper.toModel(savedEmployee);
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating employee: " + e.getMessage(), e);
        }
    }

    @Transactional
    public EmployeeModel update(Long id, EmployeeModel employeeModel) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Store the old state for audit
        Employee oldState = new Employee();
        BeanUtils.copyProperties(existing, oldState);

        // Check permissions based on role
        if (securityUtils.hasRole("ROLE_MANAGER") && !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            if (!existing.getDepartment().equals(managerDepartment)) {
                throw new AccessDeniedException("Access denied: Cannot update employee from different department");
            }

            // Managers can only update specific fields
            validateManagerUpdate(existing, employeeModel);
        }

        if (!existing.getEmployeeId().equals(employeeModel.getEmployeeId()) &&
                employeeRepository.existsByEmployeeId(employeeModel.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + employeeModel.getEmployeeId());
        }

        employeeMapper.updateEntityFromModel(existing, employeeModel);
        Employee savedEmployee = employeeRepository.save(existing);


        return employeeMapper.toModel(savedEmployee);
    }

    private void validateManagerUpdate(Employee existing, EmployeeModel updated) {
        // Managers cannot change these fields
        if (!existing.getEmployeeId().equals(updated.getEmployeeId()) ||
                !existing.getDepartment().equals(updated.getDepartment()) ||
                !existing.getHireDate().equals(updated.getHireDate()) ||
                existing.getStatus() != updated.getStatus()) {
            throw new AccessDeniedException("Access denied: Managers can only update basic employee information");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            throw new AccessDeniedException("Access denied: Only HR Personnel and Admins can delete employees");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getUser() != null) {
            User user = employee.getUser();
            user.setEmployee(null);
            employee.setUser(null);
            userRepository.save(user);
        }

        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeModel> search(String query, Pageable pageable) {
        Page<Employee> employeePage;

        if (securityUtils.hasRole("ROLE_MANAGER") && !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            employeePage = employeeRepository.searchInDepartment(query.trim(), managerDepartment, pageable);
        } else if (query == null || query.trim().isEmpty()) {
            employeePage = employeeRepository.findAll(pageable);
        } else {
            employeePage = employeeRepository.search(query.trim(), pageable);
        }

        return employeeMapper.toModelPage(employeePage);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(String employeeId) {
        // Everyone can check if an employee ID exists, but managers can only check within their department
        if (securityUtils.hasRole("ROLE_MANAGER") && !securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")) {
            String managerDepartment = securityUtils.getCurrentUserDepartment();
            // For managers, we need to check both existence and department
            return employeeRepository.findByEmployeeId(employeeId)
                    .map(employee -> employee.getDepartment().equals(managerDepartment))
                    .orElse(false);
        }

        return employeeRepository.existsByEmployeeId(employeeId);
    }
}