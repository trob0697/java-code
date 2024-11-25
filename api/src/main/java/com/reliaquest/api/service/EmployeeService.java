package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDeleteRequest;
import com.reliaquest.api.model.EmployeePostRequest;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
    private static final String EMPLOYEE_API_URL = "http://localhost:8112/api/v1/employee";
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY_IN_MILLISECONDS = 10000;

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        EmployeeResponse<List<Employee>> response = sendRequest(
                EMPLOYEE_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>() {});
        return response.getData();
    }

    public List<Employee> getAllEmployeesWithName(String searchString) {
        return getAllEmployees().stream()
                .filter(e -> e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        EmployeeResponse<Employee> response = sendRequest(
                EMPLOYEE_API_URL + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<EmployeeResponse<Employee>>() {});
        return response.getData();
    }

    public int getHighestSalary() {
        return getAllEmployees().stream()
                .mapToInt(Employee::getEmployeeSalary)
                .max()
                .orElseThrow();
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeePostRequest requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        EmployeeResponse<Employee> response = sendRequest(
                EMPLOYEE_API_URL,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<EmployeeResponse<Employee>>() {});
        return response.getData();
    }

    public boolean deleteEmployeeById(String id) {
        EmployeeDeleteRequest requestBody = new EmployeeDeleteRequest();
        requestBody.setName(getEmployeeById(id).getEmployeeName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        EmployeeResponse<Boolean> response = sendRequest(
                EMPLOYEE_API_URL,
                HttpMethod.DELETE,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<EmployeeResponse<Boolean>>() {});
        return response.getData();
    }

    private <T, R> R sendRequest(
            String url,
            HttpMethod method,
            HttpEntity<Object> requestEntity,
            ParameterizedTypeReference<R> responseType) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.println("Attempt " + (attempt + 1) + ": [" + method.toString() + "] " + url);
                ResponseEntity<R> response = restTemplate.exchange(url, method, requestEntity, responseType);
                return response.getBody();
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_IN_MILLISECONDS);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        System.out.println("Retry delay interrupted");
                    }
                }
            } catch (HttpClientErrorException.NotFound e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Failed access external api");
    }
}
