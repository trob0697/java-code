package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EmployeePostRequest {

    @NotBlank
    private String name;

    @Positive @NotNull private int salary;

    @Min(16)
    @Max(75)
    @NotNull private int age;

    @NotBlank
    private String title;
}
