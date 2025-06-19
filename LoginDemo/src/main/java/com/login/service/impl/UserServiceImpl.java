package com.login.service.impl;

import com.login.dto.UserDto;
import com.login.entity.Role;
import com.login.entity.User;
import com.login.exception.UserException;
import com.login.repository.RoleRepository;
import com.login.repository.UserRepository;
import com.login.service.UserService;
import com.login.utils.ModelMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMappers modelMappers;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMappers modelMappers) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMappers = modelMappers;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User save(UserDto userDto) {
        // Encode password before mapping
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user = modelMappers.dtoToUser(userDto);
        Set<Role> roles = getOrCreateRolesFromNames(userDto.getRoles());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    private Set<Role> getOrCreateRolesFromNames(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName);
                if (role == null) {
                    role = new Role(roleName);
                    roleRepository.save(role);
                }
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepository.findByName("ROLE_USER");
            if (defaultRole == null) {
                defaultRole = new Role("ROLE_USER");
                roleRepository.save(defaultRole);
            }
            roles.add(defaultRole);
        }
        return roles;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public String registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            return "Username already exists";
        }
        save(userDto);
        return null;
    }

    @Override
    public void populateUserHomeModel(Model model, Principal principal) {
        User user = findByUsername(principal.getName());
        model.addAttribute("userdetail", user);

        user.getRoles().forEach(role -> {
            String roleName = role.getName();
            model.addAttribute("role", roleName);
            switch (roleName) {
                case "ROLE_ADMIN":
                    model.addAttribute("isAdmin", true);
                    break;
                case "ROLE_MANAGER":
                    model.addAttribute("isManager", true);
                    break;
                default:
                    model.addAttribute("isUser", true);
                    break;
            }
        });
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User Not Found for given id " + id));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserException("User Not Found for given id " + userDto.getId()));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setFullname(userDto.getFullname());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        Set<Role> roles = getOrCreateRolesFromNames(userDto.getRoles());
        existingUser.setRoles(roles);

        userRepository.save(existingUser);
    }
}
