package com.reliaquest.api.model;

import lombok.Data;

@Data
public class EmployeeResponse<T> {
    private T data;
    private String status;
}
