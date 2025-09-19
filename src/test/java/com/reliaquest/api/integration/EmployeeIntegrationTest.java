package com.reliaquest.api.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "server.port=0")
class EmployeeIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void getAllEmployees_ShouldReturnList() {
    ResponseEntity<List<Employee>> response =
        restTemplate.exchange(
            "/api/v1/employee",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Employee>>() {});

    assertNotNull(response);
  }

  @Test
  void createEmployee_ShouldReturnCreatedEmployee() {
    EmployeeInput input = new EmployeeInput("Test Employee", 60000, 30, "Test Title");

    ResponseEntity<Employee> response =
        restTemplate.postForEntity("/api/v1/employee", input, Employee.class);

    assertNotNull(response);
  }
}
