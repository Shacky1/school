package com.shacv.school.dto;

import com.shacv.school.entity.Marks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MarksDto {
    private Long studentId;
    private Double score;
    private Marks.Grade grade;


}
