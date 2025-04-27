package com.balanceeat.demo.domain.user.mapper;

import com.balanceeat.demo.domain.user.dto.UserDTO;
import com.balanceeat.demo.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserById(String id);
    void updateUser(UserDTO userDTO);
    void deleteUser(String id);
    User findByUsername(String username);
    void insert(User user);
}