package com.login.utils;

import com.login.dto.RoleDto;
import com.login.dto.UserDto;
import com.login.entity.Role;
import com.login.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

@Component
public class ModelMappers {

    @Autowired
    private ModelMapper modelMapper;

    public UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User dtoToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public RoleDto roleToDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

    public Role dtoToRole(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }
}
