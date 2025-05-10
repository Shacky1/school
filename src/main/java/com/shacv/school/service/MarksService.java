package com.shacv.school.service;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Marks;
import com.shacv.school.entity.Student;
import com.shacv.school.repository.MarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarksService {

    @Autowired
    private MarksRepository marksRepository;

    public void saveMarks(Marks marks) {
        marksRepository.save(marks);
    }
    public Marks findByStudentAndCourse(Student student, Course course) {
        return marksRepository.findByStudentAndCourse(student, course);
    }


}
