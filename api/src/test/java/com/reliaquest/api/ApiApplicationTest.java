package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDeleteRequest;
import com.reliaquest.api.model.EmployeePostRequest;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
class ApiApplicationTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> mockEmployees;

    private final String EMPLOYEE_API_URL = "http://localhost:8112/api/v1/employee";

    @BeforeAll
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        mockEmployees = objectMapper.readValue(
                new File("src/test/java/com/reliaquest/resources/mockEmployees.json"),
                new ObjectMapper().getTypeFactory().constructParametricType(List.class, Employee.class));
    }

    @Test
    void getAllEmployees() {
        EmployeeResponse<List<Employee>> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(mockEmployees);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        List<Employee> response = employeeService.getAllEmployees();
        assertEquals(50, response.size());
        assertEquals(mockEmployees, response);
    }

    @Test
    void getAllEmployeesWithName() {
        EmployeeResponse<List<Employee>> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(mockEmployees);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        List<Employee> response = employeeService.getAllEmployeesWithName("Mrs");
        assertEquals(3, response.size());
        assertEquals("Mrs. Joel DuBuque", response.get(0).getEmployeeName());
        assertEquals("Mrs. Takako Mante", response.get(1).getEmployeeName());
        assertEquals("Mrs. Refugio Roob", response.get(2).getEmployeeName());
    }

    @Test
    void getEmployeeById() {
        Employee mockEmployee = mockEmployees.get(0);
        EmployeeResponse<Employee> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(mockEmployee);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL + "/" + mockEmployee.getId(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<Employee>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Employee response = employeeService.getEmployeeById(mockEmployee.getId());
        assertEquals(mockEmployee.getEmployeeName(), response.getEmployeeName());
    }

    @Test
    void getHighestSalary() {
        EmployeeResponse<List<Employee>> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(mockEmployees);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        int response = employeeService.getHighestSalary();
        assertEquals(497858, response);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() {
        EmployeeResponse<List<Employee>> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(mockEmployees);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        List<String> response = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(
                Arrays.asList(
                        "Vincenzo Morissette",
                        "Michale Hodkiewicz",
                        "Mrs. Joel DuBuque",
                        "Eddy Schulist",
                        "Krystina Davis",
                        "Meda Treutel",
                        "Antoine Koelpin",
                        "Blythe McClure",
                        "Claud Hills",
                        "Ivan Johnson III"),
                response);
    }

    @Test
    void createEmployee() {
        Employee newEmployee = new Employee();
        newEmployee.setId("f42920f2-bd03-4795-aa32-dc1bfd568d0e");
        newEmployee.setEmployeeName("Joe Shmo");
        newEmployee.setEmployeeSalary(1000000);
        newEmployee.setEmployeeAge(60);
        newEmployee.setEmployeeTitle("Dish washer");
        newEmployee.setEmployeeEmail("joe.shmo@email.com");

        EmployeePostRequest requestBody = new EmployeePostRequest();
        requestBody.setName(newEmployee.getEmployeeName());
        requestBody.setSalary(newEmployee.getEmployeeSalary());
        requestBody.setAge(newEmployee.getEmployeeAge());
        requestBody.setTitle(newEmployee.getEmployeeTitle());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EmployeeResponse<Employee> mockResponse = new EmployeeResponse<>();
        mockResponse.setData(newEmployee);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, headers),
                        new ParameterizedTypeReference<EmployeeResponse<Employee>>() {}))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Employee response = employeeService.createEmployee(requestBody);
        assertEquals(newEmployee, response);
    }

    @Test
    void deleteEmployeeById() {
        Employee deleteEmployee = mockEmployees.get(mockEmployees.size() - 1);

        EmployeeResponse<Employee> mockGetResponse = new EmployeeResponse<>();
        mockGetResponse.setData(deleteEmployee);

        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL + "/" + deleteEmployee.getId(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<EmployeeResponse<Employee>>() {}))
                .thenReturn(ResponseEntity.ok(mockGetResponse));

        EmployeeDeleteRequest requestBody = new EmployeeDeleteRequest();
        requestBody.setName(deleteEmployee.getEmployeeName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EmployeeResponse<Boolean> mockDeleteResponse = new EmployeeResponse<>();
        mockDeleteResponse.setData(true);
        Mockito.when(restTemplate.exchange(
                        EMPLOYEE_API_URL,
                        HttpMethod.DELETE,
                        new HttpEntity<>(requestBody, headers),
                        new ParameterizedTypeReference<EmployeeResponse<Boolean>>() {}))
                .thenReturn(ResponseEntity.ok(mockDeleteResponse));

        Boolean response = employeeService.deleteEmployeeById(deleteEmployee.getId());
        assertEquals(true, response);
    }
}
