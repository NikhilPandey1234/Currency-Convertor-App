package com.login.service;

import com.login.dto.UserDto;
import com.login.entity.User;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

public interface UserService {
    User findByUsername(String username);

    User save(UserDto userDto);

    List<User> findAll();

    String registerUser(UserDto userDto);

    void populateUserHomeModel(Model model, Principal principal);

    User findById(Long id);

    void deleteById(Long id);

    void update(UserDto userDto);

}
