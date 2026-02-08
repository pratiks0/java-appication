package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
     * Everyone can view (even regular users)
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
     * ADMIN ONLY
     */
    @GetMapping("/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course-form";
    }

    /**
     * Show form for editing an existing course
     * ADMIN ONLY
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
     * ADMIN ONLY
     */
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
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
     * ADMIN ONLY
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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