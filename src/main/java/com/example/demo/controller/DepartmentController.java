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
     */
    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    /**
     * Show form for adding a new department
     */
    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("department", new DepartmentDto());
        return "department-form";
    }

    /**
     * Show form for editing an existing department
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        
        DepartmentDto dto = new DepartmentDto();
        dto.setName(department.getName());
        
        model.addAttribute("department", dto);
        model.addAttribute("departmentId", id);
        return "department-form";
    }

    /**
     * Save or update department
     */
    @PostMapping("/save")
    public String saveDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                BindingResult result,
                                @RequestParam(required = false) Long departmentId,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (result.hasErrors()) {
            if (departmentId != null) {
                model.addAttribute("departmentId", departmentId);
            }
            return "department-form";
        }

        try {
            Department department = new Department();
            department.setName(dto.getName());
            
            if (departmentId != null) {
                // Update existing
                departmentService.updateDepartment(departmentId, department);
                redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
            } else {
                // Create new
                departmentService.saveDepartment(department);
                redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving department: " + e.getMessage());
        }

        return "redirect:/departments";
    }

    /**
     * Delete department
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