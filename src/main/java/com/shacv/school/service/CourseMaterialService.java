package com.shacv.school.service;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.CourseMaterial;
import com.shacv.school.entity.Employee;
import com.shacv.school.repository.CourseMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

@Service
public class CourseMaterialService {

    @Autowired
    private CourseMaterialRepository courseMaterialRepository;


    // Create a new course material with file upload
    public CourseMaterial createCourseMaterial(CourseMaterial material, MultipartFile file) {
        material.setUploadDate(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            try {
                material.setFileName(file.getOriginalFilename());
                material.setFileType(file.getContentType());
                material.setContent(file.getBytes());
                material.setSize((double) file.getSize());
            } catch (IOException e) {
                throw new MultipartException("Could not read the file: " + e.getMessage());
            }
        }

        // Perform validation checks
        validateCourseMaterial(material);

        return courseMaterialRepository.save(material);
    }

    // Validation method
    private void validateCourseMaterial(CourseMaterial material) {
        if (material.getTitle() == null || material.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (material.getCourse() == null) {
            throw new IllegalArgumentException("Course must be associated with the material");
        }
        if (material.getInstructor() == null) {
            throw new IllegalArgumentException("Instructor must be specified");
        }
    }


    // Get a course material by ID
    public Optional<CourseMaterial> getCourseMaterialById(Long id) {
        return courseMaterialRepository.findById(id);
    }

    // Update a course material (with optional file upload)
    public CourseMaterial updateCourseMaterial(Long id, CourseMaterial updatedMaterial, MultipartFile file) throws Exception {
        Optional<CourseMaterial> existingMaterialOpt = courseMaterialRepository.findById(id);

        if (existingMaterialOpt.isPresent()) {
            CourseMaterial existingMaterial = existingMaterialOpt.get();

            // Update basic fields
            existingMaterial.setTitle(updatedMaterial.getTitle());
            existingMaterial.setDescription(updatedMaterial.getDescription());
            existingMaterial.setAccessLevel(updatedMaterial.getAccessLevel());
            existingMaterial.setCourse(updatedMaterial.getCourse());
            existingMaterial.setInstructor(updatedMaterial.getInstructor());

            // If a new file is uploaded, update the file details
            if (file != null && !file.isEmpty()) {
                existingMaterial.setFileName(file.getOriginalFilename());
                existingMaterial.setFileType(file.getContentType());
                existingMaterial.setContent(file.getBytes());
                existingMaterial.setSize((double) file.getSize());
            }

            // Save the updated course material
            return courseMaterialRepository.save(existingMaterial);
        } else {
            throw new Exception("Course material not found");
        }
    }

    // Delete a course material by ID
    public void deleteCourseMaterial(Long id) throws Exception {
        if (courseMaterialRepository.existsById(id)) {
            courseMaterialRepository.deleteById(id);
        } else {
            throw new Exception("Course material not found");
        }
    }

    // Get all course materials
    public List<CourseMaterial> getAllCourseMaterials() {
        return courseMaterialRepository.findAll();
    }

    // Get all course materials created by a specific teacher (instructor)
    public List<CourseMaterial> getAllCourseMaterialsByTeacher(Employee teacher) {
        return courseMaterialRepository.findByInstructor(teacher);
    }

    // Get all course materials for courses taught by a teacher (instructor)
    public List<CourseMaterial> getAllCourseMaterialsByTeacherCourses(List<Course> courses) {
        return courseMaterialRepository.findByCourseIn(courses);
    }

    public List<CourseMaterial> getMaterialsByCourses(List<Course> courses) {
        return courseMaterialRepository.findByCourseIn(courses);

    }
    // Get all course materials for a specific course
    public List<CourseMaterial> getMaterialsByCourse(Course course) {
        return courseMaterialRepository.findByCourse(course);
    }

}


