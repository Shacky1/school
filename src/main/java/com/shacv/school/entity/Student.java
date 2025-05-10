package com.shacv.school.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    // Calculated field (not persisted in DB)
    @Transient
    private Integer yearOfStudy;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    // Enrollment date to calculate year of study
    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    @ToString.Exclude
    private Program program;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_number", referencedColumnName = "user_name")
    @ToString.Exclude
    private User user;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id")
    )
    @ToString.Exclude
    private List<Course> courses;

    // Relationship with QuizResult
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<QuizResult> quizResults;

    // Method to calculate year of study
    public Integer getYearOfStudy() {
        if (enrollmentDate == null) {
            return null;
        }
        LocalDate currentDate = LocalDate.now();
        return (int) ChronoUnit.YEARS.between(enrollmentDate, currentDate) + 1;
    }
}
