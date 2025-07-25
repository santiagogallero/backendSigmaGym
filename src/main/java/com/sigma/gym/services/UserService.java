package com.sigma.gym.services;

import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.User;

public interface UserService {
     public User createUser(RegisterRequest request) throws Exception;

    public User getUserByUsername(String username) throws Exception;

    public Page<User> getUsers(PageRequest pageRequest) throws Exception;

    public User updateUser(User user) throws Exception;

    public Optional<User> getUserById(Long userId)throws Exception;
    
}
