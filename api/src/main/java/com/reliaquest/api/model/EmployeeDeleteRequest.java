package com.reliaquest.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeDeleteRequest {
    @NotBlank
    private String name;
}
