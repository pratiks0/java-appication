package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.CourseDto;
import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Display list of all courses
     * Everyone can view
     */
    @GetMapping
    public String listCourses(Model model) {
        try {
            model.addAttribute("courses", courseService.getAllCourses());
            return "courses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading courses: " + e.getMessage());
            return "courses";
        }
    }

    /**
     * Show form for adding a new course
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course-form";
    }

    /**
     * Show form for editing an existing course
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id);
            
            CourseDto dto = new CourseDto();
            dto.setId(course.getId());
            dto.setTitle(course.getTitle());
            
            model.addAttribute("course", dto);
            return "course-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading course: " + e.getMessage());
            return "redirect:/courses";
        }
    }

    /**
     * Save or update course
     * UI allows access - Form submission will be validated by API
     * Real security is on the REST API endpoint
     */
    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute("course") CourseDto dto,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            return "course-form";
        }

        try {
            Course course;
            if (dto.getId() != null) {
                course = courseService.getCourseById(dto.getId());
                course.setTitle(dto.getTitle());
            } else {
                course = new Course(dto.getTitle());
            }
            
            courseService.saveCourse(course);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                dto.getId() != null ? "Course updated successfully!" : "Course created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving course: " + e.getMessage());
        }

        return "redirect:/courses";
    }

    /**
     * Delete course
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting course: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}