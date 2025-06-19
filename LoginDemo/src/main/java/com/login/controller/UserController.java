package com.login.controller;

import com.login.dto.UserDto;
import com.login.entity.User;
import com.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class UserController {
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public UserController(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model) {
        String result = userService.registerUser(userDto);
        if (result != null) {
            model.addAttribute("error", result);
            return "register";
        }
        return "redirect:/register?success";
    }

    public String listUsers(Model model) {
        List<User> userDtos = userService.findAll();
        model.addAttribute("users", userDtos);
        return "user/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users?error=UserNotFound";
        }
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        userService.update(userDto);
        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/users";
    }

}