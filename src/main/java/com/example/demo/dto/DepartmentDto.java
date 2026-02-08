package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentDto {

    @NotBlank(message = "Department name is required")
    private String name;

    // Constructors
    public DepartmentDto() {
    }

    public DepartmentDto(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
