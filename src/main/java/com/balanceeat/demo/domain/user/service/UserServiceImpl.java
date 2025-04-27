package com.balanceeat.demo.domain.user.service;

import com.balanceeat.demo.domain.user.dto.UserDTO;
import com.balanceeat.demo.domain.user.entity.User;
import com.balanceeat.demo.domain.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserDTO getUserById(String userId) {
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return null;
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId().toString());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }
    
    @Override
    public void updateUser(UserDTO userDTO) {
        userMapper.updateUser(userDTO);
    }
    
    @Override
    public void deleteUser(String userId) {
        userMapper.deleteUser(userId);
    }
} 