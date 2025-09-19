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
    try {
      Employee employee = apiClient.getEmployeeById(id);
      if (employee == null) {
        log.warn("Employee not found with id: {}", id);
        throw new EmployeeNotFoundException("Employee not found with id: " + id);
      }
      return employee;
    } catch (ExternalServiceException e) {
      log.error("Failed to fetch employee by id: {}", id, e);
      throw e;
    }
  }

  public Employee createEmployee(EmployeeInput input) {
    log.info("Creating employee: {}", input.name());
    try {
      Employee employee = apiClient.createEmployee(input);
      if (employee == null) {
        throw new ExternalServiceException(
            "Failed to create employee - no response from service", null);
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

  public List<Employee> searchEmployeesByName(String searchString) {
    log.info("Searching employees by name: {}", searchString);
    try {
      List<Employee> employees = apiClient.getAllEmployees();
      return employees.stream()
          .filter(
              emp ->
                  emp.name() != null
                      && emp.name()
                          .toLowerCase(java.util.Locale.ROOT)
                          .contains(searchString.toLowerCase(java.util.Locale.ROOT)))
          .toList();
    } catch (ExternalServiceException e) {
      log.error("Failed to search employees by name: {}", searchString, e);
      throw e;
    }
  }

  public String deleteEmployeeById(String id) {
    log.info("Deleting employee by id: {}", id);
    try {
      Employee employee = getEmployeeById(id);
      boolean deleted = apiClient.deleteEmployee(employee.name());
      if (!deleted) {
        throw new ExternalServiceException("Failed to delete employee with id: " + id);
      }
      return employee.name();
    } catch (ExternalServiceException e) {
      log.error("Failed to delete employee by id: {}", id, e);
      throw e;
    }
  }
}
