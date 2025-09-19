package com.reliaquest.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private EmployeeService employeeService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void getAllEmployees_ShouldReturnEmployeeList() throws Exception {
    Employee emp1 = new Employee("1", "John Doe", 50000, null, null, null);

    List<Employee> employees = Arrays.asList(emp1);
    when(employeeService.getAllEmployees()).thenReturn(employees);

    mockMvc
        .perform(get("/api/v1/employee"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("1"))
        .andExpect(jsonPath("$[0].employee_name").value("John Doe"));
  }

  @Test
  void getEmployeeById_ShouldReturnEmployee() throws Exception {
    Employee emp = new Employee("1", "John Doe", null, null, null, null);

    when(employeeService.getEmployeeById("1")).thenReturn(emp);

    mockMvc
        .perform(get("/api/v1/employee/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"));
  }

  @Test
  void getEmployeeById_NotFound_ShouldReturn404() throws Exception {
    when(employeeService.getEmployeeById("999"))
        .thenThrow(
            new com.reliaquest.api.exception.EmployeeNotFoundException("Employee not found"));

    mockMvc.perform(get("/api/v1/employee/999")).andExpect(status().isNotFound());
  }

  @Test
  void getEmployeesByNameSearch_ShouldReturnFilteredList() throws Exception {
    Employee emp1 = new Employee(null, "John Doe", null, null, null, null);

    when(employeeService.searchEmployeesByName("john")).thenReturn(Arrays.asList(emp1));

    mockMvc
        .perform(get("/api/v1/employee/search/john"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getEmployeesByNameSearch_EmptyString_ShouldReturn400() throws Exception {
    mockMvc.perform(get("/api/v1/employee/search/ ")).andExpect(status().isBadRequest());
  }

  @Test
  void getHighestSalaryOfEmployees_ShouldReturnMaxSalary() throws Exception {
    Employee emp1 = new Employee(null, null, 50000, null, null, null);
    Employee emp2 = new Employee(null, null, 75000, null, null, null);

    when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(emp1, emp2));

    mockMvc
        .perform(get("/api/v1/employee/highestSalary"))
        .andExpect(status().isOk())
        .andExpect(content().string("75000"));
  }

  @Test
  void getTopTenHighestEarningEmployeeNames_ShouldReturnNames() throws Exception {
    Employee emp1 = new Employee(null, "John Doe", 75000, null, null, null);
    Employee emp2 = new Employee(null, "Jane Smith", 50000, null, null, null);

    when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(emp1, emp2));

    mockMvc
        .perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("John Doe"))
        .andExpect(jsonPath("$[1]").value("Jane Smith"));
  }

  @Test
  void createEmployee_ValidInput_ShouldReturnEmployee() throws Exception {
    com.reliaquest.api.model.EmployeeInput validInput =
        new com.reliaquest.api.model.EmployeeInput("John Doe", 50000, 25, "Developer");
    Employee createdEmployee =
        new Employee("1", "John Doe", 50000, 25, "Developer", "john@company.com");

    when(employeeService.createEmployee(validInput)).thenReturn(createdEmployee);

    mockMvc
        .perform(
            post("/api/v1/employee")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validInput)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.employee_name").value("John Doe"));
  }

  @Test
  void createEmployee_InvalidInput_ShouldReturn400() throws Exception {
    com.reliaquest.api.model.EmployeeInput invalidInput =
        new com.reliaquest.api.model.EmployeeInput("", -1000, 25, "Title");

    mockMvc
        .perform(
            post("/api/v1/employee")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidInput)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteEmployeeById_ShouldReturnEmployeeName() throws Exception {
    when(employeeService.deleteEmployeeById("1")).thenReturn("John Doe");

    mockMvc
        .perform(delete("/api/v1/employee/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("John Doe"));
  }
}
