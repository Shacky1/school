package com.shacv.school.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "programs")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer duration;
    private String description;

    @OneToMany(mappedBy = "program", orphanRemoval = true)
    @ToString.Exclude // Prevent recursive toString
    private List<Student> students;

    @OneToMany(mappedBy = "program", orphanRemoval = true)
    @ToString.Exclude // Prevent recursive toString
    private List<Course> courses;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @ToString.Exclude // Prevent recursive toString
    private Department department;

}
