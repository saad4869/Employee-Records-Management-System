package com.hahn.erms.security;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.entity.User;
import com.hahn.erms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roleWithPrefix = ensureRolePrefix(role);
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals(roleWithPrefix));
    }

    public boolean hasAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.warn("Authentication is null in hasAnyRole check");
            return false;
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> {
                    for (String role : roles) {
                        String prefixedRole = ensureRolePrefix(role);
                        if (authority.equals(prefixedRole)) {
                            log.debug("Found matching role: {} for authority: {}",
                                    prefixedRole, authority);
                            return true;
                        }
                    }
                    return false;
                });
    }

    private String ensureRolePrefix(String role) {
        if (!role.startsWith("ROLE_")) {
            return "ROLE_" + role;
        }
        return role;
    }

    public String getCurrentUserDepartment() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        return userRepository.findByUsername(auth.getName())
                .map(User::getEmployee)
                .map(Employee::getDepartment)
                .orElse(null);
    }

}