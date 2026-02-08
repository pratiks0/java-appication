package com.example.demo.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.StudentRequestDto;
import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentProfile;
import com.example.demo.service.CourseService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.StudentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;

    public StudentController(StudentService studentService, 
                            DepartmentService departmentService,
                            CourseService courseService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.courseService = courseService;
    }

    
    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    
    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("student", new StudentRequestDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        return "student-form";
    }

    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);

        StudentRequestDto dto = new StudentRequestDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());

        if (student.getProfile() != null) {
            dto.setAddress(student.getProfile().getAddress());
            dto.setPhone(student.getProfile().getPhone());
        }

        // Set department ID
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
        }

        // Set course IDs as comma-separated string
        if (student.getCourses() != null && !student.getCourses().isEmpty()) {
            String courseIds = student.getCourses().stream()
                    .map(c -> c.getId().toString())
                    .collect(Collectors.joining(","));
            dto.setCourseIds(courseIds);
        }

        model.addAttribute("student", dto);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        return "student-form";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("student") StudentRequestDto dto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("courses", courseService.getAllCourses());
            return "student-form";
        }

        try {
            // Fetch existing student if updating, or create new one
            Student student;
            if (dto.getId() != null) {
                student = studentService.getStudentById(dto.getId());
            } else {
                student = new Student();
            }

            // Set basic fields
            student.setName(dto.getName());
            student.setEmail(dto.getEmail());

            // Prepare profile data
            StudentProfile profileData = new StudentProfile();
            profileData.setAddress(dto.getAddress());
            profileData.setPhone(dto.getPhone());

            // Parse course IDs
            Set<Long> courseIds = new HashSet<>();
            if (dto.getCourseIds() != null && !dto.getCourseIds().isBlank()) {
                String[] ids = dto.getCourseIds().split(",");
                for (String id : ids) {
                    try {
                        courseIds.add(Long.parseLong(id.trim()));
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }

            // Save student with all relationships
            studentService.saveStudent(student, dto.getDepartmentId(), courseIds, profileData);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                dto.getId() != null ? "Student updated successfully!" : "Student created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving student: " + e.getMessage());
        }

        return "redirect:/students";
    }

    
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting student: " + e.getMessage());
        }
        return "redirect:/students";
    }
}
