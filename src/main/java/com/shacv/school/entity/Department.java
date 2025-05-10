package com.shacv.school.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @ToString.Exclude // Prevent recursive toString
    private List<Employee> employees;

    @OneToMany(mappedBy = "department")
    @ToString.Exclude // Prevent recursive toString
    private List<Program> programs;
}
