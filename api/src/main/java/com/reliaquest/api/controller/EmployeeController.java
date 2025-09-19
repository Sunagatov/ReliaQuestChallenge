package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Request to get all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("Request to search employees by name: {}", searchString);
        if (searchString == null || searchString.isBlank()) {
            log.warn("Invalid search string provided");
            return ResponseEntity.badRequest().build();
        }
        List<Employee> employees = employeeService.getAllEmployees();
        List<Employee> filtered = employees.stream()
                .filter(emp -> emp.name() != null && 
                        emp.name().toLowerCase(java.util.Locale.ROOT).contains(searchString.toLowerCase(java.util.Locale.ROOT)))
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("Request to get employee by id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Request to get highest salary of employees");
        List<Employee> employees = employeeService.getAllEmployees();
        Integer highestSalary = employees.stream()
                .filter(emp -> emp.salary() != null)
                .mapToInt(Employee::salary)
                .max()
                .orElse(0);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Request to get top 10 highest earning employee names");
        List<Employee> employees = employeeService.getAllEmployees();
        List<String> topTen = employees.stream()
                .filter(emp -> emp.salary() != null && emp.name() != null)
                .sorted(Comparator.comparing(Employee::salary).reversed())
                .limit(10)
                .map(Employee::name)
                .toList();
        return ResponseEntity.ok(topTen);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeInput employeeInput) {
        log.info("Request to create employee: {}", employeeInput.name());
        Employee employee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("Request to delete employee by id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        boolean deleted = employeeService.deleteEmployee(employee.name());
        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employee.name());
    }
}
