package com.login.controller;

import com.login.dto.RoleDto;
import com.login.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public String listRoles(Model model) {
        List<RoleDto> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "role/list";
    }

    @GetMapping("/new")
    public String createRoleForm(Model model) {
        model.addAttribute("role", new RoleDto());
        return "role/form";
    }

    @PostMapping("/save")
    public String saveRole(@ModelAttribute("role") RoleDto roleDto) {
        roleService.save(roleDto);
        return "redirect:/roles";
    }

    @GetMapping("/edit/{id}")
    public String editRoleForm(@PathVariable Long id, Model model) {
        RoleDto roleDto = roleService.getRoleById(id);
        model.addAttribute("role", roleDto);
        return "role/form";
    }

    @PostMapping("/update")
    public String updateRole(@ModelAttribute("role") RoleDto roleDto) {
        roleService.updateRole(roleDto);
        return "redirect:/roles";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return "redirect:/roles";
    }
}
