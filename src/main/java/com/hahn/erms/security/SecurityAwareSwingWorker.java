package com.hahn.erms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.SwingWorker;

public abstract class SecurityAwareSwingWorker<T, V> extends SwingWorker<T, V> {
    private final Authentication authentication;

    public SecurityAwareSwingWorker() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    protected final T doInBackground() throws Exception {
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return doSecuredWork();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    protected abstract T doSecuredWork() throws Exception;
}