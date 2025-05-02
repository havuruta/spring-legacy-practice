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
        return UserDTO.from(userMapper.getUserById(userId));
    }
    
    @Override
    public void updateUser(UserDTO userDTO) {
        if (!findByUsername(userDTO.getUsername())) {
            userMapper.updateUser(userDTO);
        } else {
            throw new RuntimeException("동일한 아이디가 있습니다. 다른 아이디로 변경하세요");
        }
    }

    @Override
    public boolean findByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }
} 