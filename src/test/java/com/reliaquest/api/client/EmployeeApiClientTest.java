package com.reliaquest.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class EmployeeApiClientTest {

  @Mock private RestClient restClient;

  @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock private RestClient.ResponseSpec responseSpec;

  @InjectMocks private EmployeeApiClient apiClient;

  @Test
  void getAllEmployees_ShouldReturnEmployeeList() {
    ReflectionTestUtils.setField(apiClient, "baseUrl", "http://test");
    Employee employee = new Employee("1", "John", 50000, 30, "Dev", "john@test.com");
    ApiResponse<List<Employee>> response = new ApiResponse<>(List.of(employee), "success");

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri("http://test")).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);

    List<Employee> result = apiClient.getAllEmployees();

    assertEquals(1, result.size());
    assertEquals("John", result.get(0).name());
  }
}
