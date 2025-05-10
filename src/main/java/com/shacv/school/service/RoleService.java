package com.shacv.school.service;

import com.shacv.school.entity.Role;
import com.shacv.school.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    public void saveRole(Role role){
        roleRepository.save(role);
    }

    public Role findRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName);
    }

    public List<Role> getAllRoles() {
        // Fetch all roles and filter out the 'STUDENT' role
        return roleRepository.findAll()
                .stream()
                .filter(role -> !role.getName().equalsIgnoreCase("STUDENT"))
                .collect(Collectors.toList());
    }
}
