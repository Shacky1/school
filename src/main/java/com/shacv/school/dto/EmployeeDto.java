package com.shacv.school.dto;

import com.shacv.school.entity.Department;
import com.shacv.school.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private String firstName;
    private String secondName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Double salary;
    private String repeatPassword;
    private Department department;
    private UserDto user;
    private Role role;
}
