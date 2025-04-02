package com.example.healthtracker.service;

import com.example.healthtracker.model.User;
import java.util.List;

public interface UserService {
    
    List<User> findAllUsers();
    
    User findUserById(Long id);
    
    User findUserByUsername(String username);
    
    User saveUser(User user);
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
    
    User createUser(String username, String password, String firstName, String lastName, boolean isAdmin);
} 