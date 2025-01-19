package com.hahn.erms.repository;

import com.hahn.erms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);


    @Query("SELECT u FROM User u JOIN u.employee e WHERE e.department = :department")
    Set<User> findByDepartment(@Param("department") String department);
}