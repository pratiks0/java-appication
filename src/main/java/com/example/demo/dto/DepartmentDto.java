package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentDto {
    
    private Long id;
    
    @NotBlank(message = "Department name is required")
    private String name;

    // Constructors
    public DepartmentDto() {
    }

    public DepartmentDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public DepartmentDto(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}