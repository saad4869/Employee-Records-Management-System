package com.hahn.erms;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.enums.EmploymentStatus;
import com.hahn.erms.mapper.EmployeeMapper;
import com.hahn.erms.repository.EmployeeRepository;
import com.hahn.erms.security.SecurityUtils;
import com.hahn.erms.service.EmployeeService;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeModel employeeModel;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("EMP001");
        employee.setFullName("John Doe");
        employee.setDepartment("IT");
        employee.setStatus(EmploymentStatus.ACTIVE);
        employee.setHireDate(LocalDate.now());

        employeeModel = new EmployeeModel();
        employeeModel.setId(1L);
        employeeModel.setEmployeeId("EMP001");
        employee.setFullName("John Doe");
        employeeModel.setDepartment("IT");
        employeeModel.setStatus(EmploymentStatus.ACTIVE);
        employeeModel.setHireDate(LocalDate.now());

        pageRequest = PageRequest.of(0, 10);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllEmployees() {
        // Arrange
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        Page<EmployeeModel> expectedPage = new PageImpl<>(List.of(employeeModel));

        when(securityUtils.hasRole("ROLE_MANAGER")).thenReturn(false);
        when(employeeRepository.findAll(pageRequest)).thenReturn(employeePage);
        when(employeeMapper.toModelPage(employeePage)).thenReturn(expectedPage);

        // Act
        Page<EmployeeModel> result = employeeService.findAll(null, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository).findAll(pageRequest);
        verify(securityUtils).hasRole("ROLE_MANAGER");
        verify(employeeMapper).toModelPage(employeePage);
    }

    @Test
    void findAll_AsManager_ReturnsOnlyDepartmentEmployees() {
        // Arrange
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        Page<EmployeeModel> expectedPage = new PageImpl<>(List.of(employeeModel));

        when(securityUtils.hasRole("ROLE_MANAGER")).thenReturn(true);
        when(securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")).thenReturn(false);
        when(securityUtils.getCurrentUserDepartment()).thenReturn("IT");
        when(employeeRepository.findByDepartment("IT", pageRequest)).thenReturn(employeePage);
        when(employeeMapper.toModelPage(employeePage)).thenReturn(expectedPage);

        // Act
        Page<EmployeeModel> result = employeeService.findAll(null, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository).findByDepartment("IT", pageRequest);
        verify(securityUtils).hasRole("ROLE_MANAGER");
        verify(securityUtils).hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR");
        verify(securityUtils).getCurrentUserDepartment();
        verify(employeeMapper).toModelPage(employeePage);
    }

    @Test
    void create_WithValidData_CreatesEmployee() {
        // Arrange
        when(securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")).thenReturn(true);
        when(employeeRepository.existsByEmployeeId(employeeModel.getEmployeeId())).thenReturn(false);
        when(employeeMapper.toEntity(employeeModel)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toModel(employee)).thenReturn(employeeModel);

        // Act
        EmployeeModel result = employeeService.create(employeeModel);

        // Assert
        assertNotNull(result);
        assertEquals(employeeModel.getEmployeeId(), result.getEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void create_WithoutPermission_ThrowsAccessDeniedException() {
        // Arrange
        when(securityUtils.hasAnyRole("ROLE_HR_PERSONNEL", "ROLE_ADMINISTRATOR")).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> employeeService.create(employeeModel));
    }
}