package com.hahn.erms.mapper;

import com.hahn.erms.entity.Employee;
import com.hahn.erms.ui.swing.mvc.model.EmployeeModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeModel toModel(Employee entity) {
        if (entity == null) {
            return null;
        }

        EmployeeModel model = new EmployeeModel();
        model.setId(entity.getId());
        model.setEmployeeId(entity.getEmployeeId());
        model.setFullName(entity.getFullName());
        model.setJobTitle(entity.getJobTitle());
        model.setDepartment(entity.getDepartment());
        model.setHireDate(entity.getHireDate());
        model.setStatus(entity.getStatus());
        model.setEmail(entity.getEmail());
        model.setPhone(entity.getPhone());
        model.setAddress(entity.getAddress());

        return model;
    }

    public Employee toEntity(EmployeeModel model) {
        if (model == null) {
            return null;
        }

        Employee entity = new Employee();
        entity.setId(model.getId());
        entity.setEmployeeId(model.getEmployeeId());
        entity.setFullName(model.getFullName());
        entity.setJobTitle(model.getJobTitle());
        entity.setDepartment(model.getDepartment());
        entity.setHireDate(model.getHireDate());
        entity.setStatus(model.getStatus());
        entity.setEmail(model.getEmail());
        entity.setPhone(model.getPhone());
        entity.setAddress(model.getAddress());

        return entity;
    }

    public Page<EmployeeModel> toModelPage(Page<Employee> entityPage) {
        return entityPage.map(this::toModel);
    }

    public void updateEntityFromModel(Employee entity, EmployeeModel model) {
        if (entity != null && model != null) {
            entity.setEmployeeId(model.getEmployeeId());
            entity.setFullName(model.getFullName());
            entity.setJobTitle(model.getJobTitle());
            entity.setDepartment(model.getDepartment());
            entity.setHireDate(model.getHireDate());
            entity.setStatus(model.getStatus());
            entity.setEmail(model.getEmail());
            entity.setPhone(model.getPhone());
            entity.setAddress(model.getAddress());
        }
    }
}