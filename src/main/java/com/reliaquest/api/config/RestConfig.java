package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

  @Value("${employee.api.timeout:5000}")
  private int timeout;

  @Bean
  public RestClient restClient() {
    org.springframework.http.client.SimpleClientHttpRequestFactory factory =
        new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(java.time.Duration.ofMillis(timeout));
    factory.setReadTimeout(java.time.Duration.ofMillis(timeout));
    return RestClient.builder().requestFactory(factory).build();
  }
}
