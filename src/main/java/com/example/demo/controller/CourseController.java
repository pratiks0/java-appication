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
     */
    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses";
    }

    /**
     * Show form for adding a new course
     */
    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course-form";
    }

    /**
     * Show form for editing an existing course
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        
        CourseDto dto = new CourseDto();
        dto.setTitle(course.getTitle());
        
        model.addAttribute("course", dto);
        model.addAttribute("courseId", id);
        return "course-form";
    }

    /**
     * Save or update course
     */
    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute("course") CourseDto dto,
                            BindingResult result,
                            @RequestParam(required = false) Long courseId,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            if (courseId != null) {
                model.addAttribute("courseId", courseId);
            }
            return "course-form";
        }

        try {
            Course course = new Course();
            course.setTitle(dto.getTitle());
            
            if (courseId != null) {
                // Update existing
                courseService.updateCourse(courseId, course);
                redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
            } else {
                // Create new
                courseService.saveCourse(course);
                redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving course: " + e.getMessage());
        }

        return "redirect:/courses";
    }

    /**
     * Delete course
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