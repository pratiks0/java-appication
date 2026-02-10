package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CourseDto {
    
    private Long id;
    
    @NotBlank(message = "Course title is required")
    private String title;

    // Constructors
    public CourseDto() {
    }

    public CourseDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public CourseDto(String title) {
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}