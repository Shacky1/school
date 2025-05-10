package com.shacv.school.dto;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Student;
import com.shacv.school.entity.Marks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMarksDto {
    private String fullName;
    private String email;
    private String phoneNumber;
    private int yearOfStudy;
    private Double score;
    private Marks.Grade grade;
    private Course course;

    public StudentMarksDto(Student student, Double score, Marks.Grade grade) {
        this.fullName = student.getFirstName() + " " + student.getLastName();
        this.email = student.getEmail();
        this.phoneNumber = student.getPhoneNumber();
        this.yearOfStudy = student.getYearOfStudy();
        this.score = score;
        this.grade = grade;

    }

    public StudentMarksDto(Course course, Double score, Marks.Grade grade) {
        this.course=course;
        this.score =score;
        this.grade=grade;
    }

    // Getters and setters can be generated here
    // ...
}
