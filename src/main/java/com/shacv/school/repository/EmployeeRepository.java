package com.shacv.school.repository;

import com.shacv.school.entity.Department;
import com.shacv.school.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    public Employee findByEmail(String userId);
    Employee findByUser_UserName(String userName);
    List<Employee> findByDepartmentAndUser_Roles_Name(Department department, String roleName);

}
