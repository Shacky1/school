package com.shacv.school.dto;

import com.shacv.school.entity.User;
import com.shacv.school.entity.Role; // Import the Role entity
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userName;
    private String password;
    private User user; // You may want to remove this if not needed
    private List<Role> roles; // Change String to Role to match entity type
}
