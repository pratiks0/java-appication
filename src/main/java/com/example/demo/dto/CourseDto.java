package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CourseDto {

    @NotBlank(message = "Course title is required")
    private String title;

    // Constructors
    public CourseDto() {
    }

    public CourseDto(String title) {
        this.title = title;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
