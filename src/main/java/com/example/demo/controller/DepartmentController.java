package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.service.DepartmentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Display list of all departments
     * Everyone can view
     */
    @GetMapping
    public String listDepartments(Model model) {
        try {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "departments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading departments: " + e.getMessage());
            return "departments";
        }
    }

    /**
     * Show form for adding a new department
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("department", new DepartmentDto());
        return "department-form";
    }

    /**
     * Show form for editing an existing department
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Department department = departmentService.getDepartmentById(id);
            
            DepartmentDto dto = new DepartmentDto();
            dto.setId(department.getId());
            dto.setName(department.getName());
            
            model.addAttribute("department", dto);
            return "department-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading department: " + e.getMessage());
            return "redirect:/departments";
        }
    }

    /**
     * Save or update department
     * UI allows access - Form submission will be validated by API
     * Real security is on the REST API endpoint
     */
    @PostMapping("/save")
    public String saveDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (result.hasErrors()) {
            return "department-form";
        }

        try {
            Department department;
            if (dto.getId() != null) {
                department = departmentService.getDepartmentById(dto.getId());
                department.setName(dto.getName());
            } else {
                department = new Department(dto.getName());
            }
            
            departmentService.saveDepartment(department);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                dto.getId() != null ? "Department updated successfully!" : "Department created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving department: " + e.getMessage());
        }

        return "redirect:/departments";
    }

    /**
     * Delete department
     * UI allows access - JavaScript will hide button for non-admins
     * Real security is on the REST API endpoint
     */
    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting department: " + e.getMessage());
        }
        return "redirect:/departments";
    }
}