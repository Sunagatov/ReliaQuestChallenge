package com.reliaquest.api.client;

import com.reliaquest.api.exception.ExternalServiceException;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeApiClient {

  private final RestClient restClient;

  @Value("${employee.api.base-url}")
  private String baseUrl;

  public List<Employee> getAllEmployees() {
    try {
      ApiResponse<List<Employee>> response =
          restClient
              .get()
              .uri(baseUrl)
              .retrieve()
              .body(new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {});
      return response != null ? response.data() : List.of();
    } catch (Exception e) {
      log.error("Error calling external API for all employees", e);
      throw new ExternalServiceException("Failed to fetch employees", e);
    }
  }

  public Employee getEmployeeById(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Employee ID cannot be null or empty");
    }
    try {
      ApiResponse<Employee> response =
          restClient
              .get()
              .uri(baseUrl + "/" + id)
              .retrieve()
              .body(new ParameterizedTypeReference<ApiResponse<Employee>>() {});
      return response != null ? response.data() : null;
    } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
      log.warn("Employee not found with id: {}", id);
      return null;
    } catch (Exception e) {
      log.error("Error calling external API for employee by id: {}", id, e);
      throw new ExternalServiceException("Failed to fetch employee", e);
    }
  }

  public Employee createEmployee(EmployeeInput input) {
    if (input == null) {
      throw new IllegalArgumentException("Employee input cannot be null");
    }
    if (input.name() == null || input.name().isBlank()) {
      throw new IllegalArgumentException("Employee name cannot be null or blank");
    }
    if (input.salary() == null || input.salary() <= 0) {
      throw new IllegalArgumentException("Employee salary must be greater than zero");
    }
    if (input.age() == null || input.age() < 16 || input.age() > 75) {
      throw new IllegalArgumentException("Employee age must be between 16 and 75");
    }
    if (input.title() == null || input.title().isBlank()) {
      throw new IllegalArgumentException("Employee title cannot be null or blank");
    }
    try {
      ApiResponse<Employee> response =
          restClient
              .post()
              .uri(baseUrl)
              .body(input)
              .retrieve()
              .body(new ParameterizedTypeReference<ApiResponse<Employee>>() {});
      return response != null ? response.data() : null;
    } catch (Exception e) {
      log.error("Error calling external API to create employee", e);
      throw new ExternalServiceException("Failed to create employee", e);
    }
  }

  public boolean deleteEmployee(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Employee name cannot be null or empty");
    }
    try {
      Map<String, String> requestBody = Map.of("name", name);
      ApiResponse<Boolean> response =
          restClient
              .method(org.springframework.http.HttpMethod.DELETE)
              .uri(baseUrl)
              .body(requestBody)
              .retrieve()
              .body(new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
      return response != null && Boolean.TRUE.equals(response.data());
    } catch (Exception e) {
      log.error("Error calling external API to delete employee: {}", name, e);
      throw new ExternalServiceException("Failed to delete employee", e);
    }
  }
}
