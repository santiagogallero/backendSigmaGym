package com.sigma.gym.services;

import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.UserEntity;

public interface UserService {
     public UserEntity createUser(RegisterRequest request) throws Exception;

    public UserEntity getUserByUsername(String username) throws Exception;

    public Page<UserEntity> getUsers(PageRequest pageRequest) throws Exception;

    public UserEntity updateUser(UserEntity user) throws Exception;

    public Optional<UserEntity> getUserById(Long userId)throws Exception;
    
}
