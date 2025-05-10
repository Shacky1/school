package com.shacv.school.service;

import com.shacv.school.dto.UserDto;
import com.shacv.school.entity.User;
import com.shacv.school.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService  {
    @Autowired
    private UserRepository userRepository;


    public void saveUser(User user) {

         userRepository.save(user);
    }
    public User findUserByUserName(String userName){
        return userRepository.findByUserName(userName);
    }


}
