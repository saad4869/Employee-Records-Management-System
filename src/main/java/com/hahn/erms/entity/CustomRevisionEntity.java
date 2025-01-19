package com.hahn.erms.entity;

import com.hahn.erms.config.CustomRevisionListener;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(name = "REVINFO_CUSTOM")
@RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity extends DefaultRevisionEntity {
    private String username;
}