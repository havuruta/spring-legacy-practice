package com.balanceeat.demo.domain.user.dto;

import com.balanceeat.demo.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserDTO {
    private final Long id;

    private final String username;

    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public static UserDTO from (User user) {
        return new UserDTO(user.getId(), user.getUsername());
    }
}