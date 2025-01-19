package com.hahn.erms.repository;

import com.hahn.erms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeId(String employeeId);

    Optional<Employee> findByEmployeeId(String employeeId);

    Page<Employee> findByDepartment(String department, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Employee> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE " +
            "(LOWER(e.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND e.department = :department")
    Page<Employee> searchInDepartment(@Param("query") String query, @Param("department") String department, Pageable pageable);
}