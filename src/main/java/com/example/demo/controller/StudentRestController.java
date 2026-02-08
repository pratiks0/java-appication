package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.StudentApiRequestDto;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentProfile;
import com.example.demo.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

   
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

   
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentApiRequestDto dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());

        // Prepare profile data
        StudentProfile profileData = null;
        if (dto.getAddress() != null || dto.getPhone() != null) {
            profileData = new StudentProfile();
            profileData.setAddress(dto.getAddress());
            profileData.setPhone(dto.getPhone());
        }

        Student savedStudent = studentService.saveStudent(
            student,
            dto.getDepartmentId(),
            dto.getCourseIds(),
            profileData
        );

        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentApiRequestDto dto) {
        
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());

        // Prepare profile data
        StudentProfile profileData = null;
        if (dto.getAddress() != null || dto.getPhone() != null) {
            profileData = new StudentProfile();
            profileData.setAddress(dto.getAddress());
            profileData.setPhone(dto.getPhone());
        }

        Student updatedStudent = studentService.updateStudent(
            id,
            student,
            dto.getDepartmentId(),
            dto.getCourseIds(),
            profileData
        );

        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
