package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock private EmployeeApiClient apiClient;

  @InjectMocks private EmployeeService employeeService;

  @Test
  void getAllEmployees_ShouldReturnEmployees() {
    List<Employee> employees =
        Arrays.asList(new Employee("1", "John", 50000, 25, "Dev", "john@test.com"));
    when(apiClient.getAllEmployees()).thenReturn(employees);

    List<Employee> result = employeeService.getAllEmployees();

    assertEquals(1, result.size());
    assertEquals("John", result.get(0).name());
  }

  @Test
  void getEmployeeById_ShouldReturnEmployee() {
    Employee employee = new Employee("1", "John", 50000, 25, "Dev", "john@test.com");
    when(apiClient.getEmployeeById("1")).thenReturn(employee);

    Employee result = employeeService.getEmployeeById("1");

    assertEquals("John", result.name());
  }

  @Test
  void getEmployeeById_NotFound_ShouldThrowException() {
    when(apiClient.getEmployeeById("999")).thenReturn(null);

    assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById("999"));
  }

  @Test
  void searchEmployeesByName_ShouldFilterEmployees() {
    List<Employee> employees =
        Arrays.asList(
            new Employee("1", "John Doe", 50000, 25, "Dev", "john@test.com"),
            new Employee("2", "Jane Smith", 60000, 30, "Manager", "jane@test.com"));
    when(apiClient.getAllEmployees()).thenReturn(employees);

    List<Employee> result = employeeService.searchEmployeesByName("john");

    assertEquals(1, result.size());
    assertEquals("John Doe", result.get(0).name());
  }

  @Test
  void createEmployee_ShouldReturnCreatedEmployee() {
    EmployeeInput input = new EmployeeInput("John", 50000, 25, "Dev");
    Employee created = new Employee("1", "John", 50000, 25, "Dev", "john@test.com");
    when(apiClient.createEmployee(input)).thenReturn(created);

    Employee result = employeeService.createEmployee(input);

    assertEquals("John", result.name());
  }

  @Test
  void deleteEmployeeById_ShouldReturnEmployeeName() {
    Employee employee = new Employee("1", "John", 50000, 25, "Dev", "john@test.com");
    when(apiClient.getEmployeeById("1")).thenReturn(employee);
    when(apiClient.deleteEmployee("John")).thenReturn(true);

    String result = employeeService.deleteEmployeeById("1");

    assertEquals("John", result);
  }
}
