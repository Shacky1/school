package com.shacv.school.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private Integer credit;
    private Integer semester;
    private String description;
    private Integer year;

    @ManyToMany(mappedBy = "courses", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<Student> students;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    @ToString.Exclude
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    @ToString.Exclude
    private Program program;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @ToString.Exclude
    private Department department;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CourseMaterial> courseMaterial;

    //Course to Quizzes
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Quiz> quizzes;
}
