package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeePostRequest;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController implements IEmployeeController<Employee, EmployeePostRequest> {

    @Autowired
    final EmployeeService employeeService = new EmployeeService();

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        List<Employee> employees = employeeService.getAllEmployeesWithName(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        int highestSalary = employeeService.getHighestSalary();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(topTenHighestEarningEmployeeNames);
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeePostRequest employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok(employee);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        boolean deleted = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(String.valueOf(deleted));
    }
}
