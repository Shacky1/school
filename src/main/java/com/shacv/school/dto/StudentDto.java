package com.shacv.school.dto;

import com.shacv.school.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Import necessary classes for UserDto
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String repeatPassword;
    private UserDto user;// Assuming you have a UserDto class defined for capturing user information.
    private Long programId;
    private String gender;
    private  String dob;
    private  String nationality;
    private String address;
}
