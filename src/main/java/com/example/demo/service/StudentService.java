package com.example.demo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentProfile;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Get all students
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Get student by ID
     */
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    /**
     * Create or update student with all relationships
     * FIXED VERSION with proper validation and cascade handling
     */
    @Transactional
    public Student saveStudent(Student student, Long departmentId, Set<Long> courseIds, StudentProfile profileData) {
        
        // Step 1: Handle Department (ManyToOne)
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
            student.setDepartment(department);
        } else {
            student.setDepartment(null);
        }

        // Step 2: Handle StudentProfile (OneToOne with CASCADE)
        if (profileData != null && (profileData.getAddress() != null || profileData.getPhone() != null)) {
            StudentProfile profile = student.getProfile();
            if (profile == null) {
                profile = new StudentProfile();
                student.setProfile(profile);
            }
            profile.setAddress(profileData.getAddress());
            profile.setPhone(profileData.getPhone());
        }

        // Step 3: IMPORTANT - Remove all existing course relationships first
        // This must be done BEFORE saving if the student already exists
        if (student.getId() != null) {
            // For existing students, we need to clear the courses and save first
            Student existingStudent = studentRepository.findById(student.getId()).orElse(null);
            if (existingStudent != null) {
                existingStudent.getCourses().clear();
                studentRepository.saveAndFlush(existingStudent);
            }
        }
        
        // Clear the courses in the current student object as well
        student.getCourses().clear();

        // Step 4: Save the student WITHOUT courses first to get/ensure ID
        // This is CRITICAL - the student must be persisted before adding ManyToMany relationships
        student = studentRepository.saveAndFlush(student);
        
        // At this point, student.getId() should NOT be null
        if (student.getId() == null) {
            throw new RuntimeException("Failed to persist student - ID is still null");
        }

        // Step 5: Now handle Courses (ManyToMany) - AFTER the student has been persisted
        if (courseIds != null && !courseIds.isEmpty()) {
            // Validate that all courses exist
            for (Long courseId : courseIds) {
                if (!courseRepository.existsById(courseId)) {
                    throw new ResourceNotFoundException("Course not found with id: " + courseId);
                }
            }
            
            // Fetch the courses
            List<Course> courses = courseRepository.findAllById(courseIds);
            
            // Double-check we got all courses
            if (courses.size() != courseIds.size()) {
                throw new ResourceNotFoundException("Some course IDs were not found. Requested: " 
                    + courseIds.size() + ", Found: " + courses.size());
            }
            
            // Add each course individually to ensure proper relationship management
            for (Course course : courses) {
                student.getCourses().add(course);
            }
            
            // Save the student again to persist the course relationships
            student = studentRepository.saveAndFlush(student);
        }

        return student;
    }

    /**
     * Update existing student
     */
    @Transactional
    public Student updateStudent(Long id, Student studentData, Long departmentId, 
                                 Set<Long> courseIds, StudentProfile profileData) {
        Student existingStudent = getStudentById(id);
        
        // Update basic fields
        existingStudent.setName(studentData.getName());
        existingStudent.setEmail(studentData.getEmail());

        // Use the common save method for relationships
        return saveStudent(existingStudent, departmentId, courseIds, profileData);
    }

    /**
     * Delete student
     */
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        
        // Get the student to clear relationships first
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            // Clear all courses to remove join table entries
            student.getCourses().clear();
            studentRepository.saveAndFlush(student);
        }
        
        studentRepository.deleteById(id);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return studentRepository.existsByEmail(email);
    }
}