package com.shacv.school.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "course_materials")
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String fileName;
    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] content;

    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude // Prevent recursive toString
    private Course course;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    @ToString.Exclude // Prevent recursive toString
    private Employee instructor;

    private Double size;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    private Integer downloadCount;


    public enum AccessLevel {
        PUBLIC, PRIVATE
    }
}
