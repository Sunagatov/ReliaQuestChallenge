package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ExternalServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeApiClient apiClient;

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        try {
            List<Employee> employees = apiClient.getAllEmployees();
            log.info("Successfully fetched {} employees", employees.size());
            return employees;
        } catch (ExternalServiceException e) {
            log.error("Failed to fetch employees from external service", e);
            throw e;
        }
    }

    public Employee getEmployeeById(String id) {
        log.info("Fetching employee by id: {}", id);
        Employee employee = apiClient.getEmployeeById(id);
        if (employee == null) {
            log.warn("Employee not found with id: {}", id);
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        return employee;
    }

    public Employee createEmployee(EmployeeInput input) {
        log.info("Creating employee: {}", input.name());
        try {
            Employee employee = apiClient.createEmployee(input);
            if (employee == null) {
                throw new ExternalServiceException("Failed to create employee - no response from service", null);
            }
            return employee;
        } catch (ExternalServiceException e) {
            log.error("Failed to create employee: {}", input.name(), e);
            throw e;
        }
    }

    public boolean deleteEmployee(String name) {
        log.info("Deleting employee: {}", name);
        try {
            return apiClient.deleteEmployee(name);
        } catch (ExternalServiceException e) {
            log.error("Failed to delete employee: {}", name, e);
            throw e;
        }
    }
}
