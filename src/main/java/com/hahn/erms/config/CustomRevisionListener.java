package com.hahn.erms.config;


import com.hahn.erms.entity.CustomRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity revision = (CustomRevisionEntity) revisionEntity;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        revision.setUsername(auth != null ? auth.getName() : "system");
    }
}