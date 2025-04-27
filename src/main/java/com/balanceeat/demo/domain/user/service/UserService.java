package com.balanceeat.demo.domain.user.service;

import com.balanceeat.demo.domain.user.dto.UserDTO;

public interface UserService {
    UserDTO getUserById(String userId);
    void updateUser(UserDTO userDTO);
    void deleteUser(String userId);
} 