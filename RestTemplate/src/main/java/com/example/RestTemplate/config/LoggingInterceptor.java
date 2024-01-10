package com.example.RestTemplate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;



public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    traceRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    traceResponse(response);
    return response;
}
  private void traceRequest(HttpRequest request, byte[] body) throws IOException {
      log.debug("Request URI: {}", request.getURI());
      log.debug("Request method: {}", request.getMethod());
      log.debug("Request headers: {}", request.getHeaders());
      log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));

}

    private void traceResponse(ClientHttpResponse response) throws IOException {
        log.debug("Response status code: {}", response.getStatusCode());
        log.debug("Response headers: {}", response.getHeaders());
        log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));

    }


}

