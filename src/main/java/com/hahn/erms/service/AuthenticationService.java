package com.hahn.erms.service;


import com.hahn.erms.entity.User;
import com.hahn.erms.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextChangedEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private Authentication currentAuthentication;
    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;


    public AuthenticationService(AuthenticationManager authenticationManager, SecurityUtils securityUtils, ApplicationEventPublisher eventPublisher) {
        this.authenticationManager = authenticationManager;
        this.securityUtils = securityUtils;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username the username
     * @param password the password
     * @return the Authentication object if successful
     * @throws AuthenticationException if authentication fails
     */

    public Authentication authenticate(String username, String password) {
        try {
            SecurityContext oldContext = SecurityContextHolder.getContext();

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            // Set the authentication in security context
             SecurityContextHolder.getContext().setAuthentication(auth);
            eventPublisher.publishEvent(new SecurityContextChangedEvent(oldContext, SecurityContextHolder.getContext()));
            return auth;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }
    /**
     * Logs out the current user and clears the security context.
     */
    public void logout() throws AuthenticationException {
        try {
            if (isAuthenticated()) {
                String username = getCurrentUsername().orElse("unknown");
                log.info("Logging out user: {}", username);

                currentAuthentication = null;
                SecurityContextHolder.clearContext();

                log.debug("User logged out successfully: {}", username);
            }
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new AuthenticationException("Logout failed: " + e.getMessage());
        }
    }

    /**
     * Checks if there is a currently authenticated user.
     *
     * @return true if a user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    /**
     * Gets the currently authenticated username.
     *
     * @return Optional containing the username if authenticated
     */
    public Optional<String> getCurrentUsername() {
        if (isAuthenticated()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Optional.ofNullable(auth.getName());
        }
        return Optional.empty();
    }

    /**
     * Gets the current user's details if authenticated.
     *
     * @return Optional containing the user details if authenticated
     */
    public Optional<User> getCurrentUser() {
        if (isAuthenticated() && currentAuthentication != null) {
            Object principal = currentAuthentication.getPrincipal();
            if (principal instanceof User) {
                return Optional.of((User) principal);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the current user has the specified role.
     *
     * @param role the role to check
     * @return true if the user has the role
     */
    public boolean hasRole(String role) {
        if (isAuthenticated()) {
            return currentAuthentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
}