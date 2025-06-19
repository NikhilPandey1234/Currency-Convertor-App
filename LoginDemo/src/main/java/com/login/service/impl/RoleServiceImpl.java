package com.login.service.impl;

import com.login.dto.RoleDto;
import com.login.entity.Role;
import com.login.exception.RoleException;
import com.login.repository.RoleRepository;
import com.login.service.RoleService;
import com.login.utils.ModelMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMappers modelMappers;


    @Override
    public RoleDto save(RoleDto roleDto) {
        Role role = modelMappers.dtoToRole(roleDto);
        role = roleRepository.save(role);
        return modelMappers.roleToDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(modelMappers::roleToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto  getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RoleException("User Role Not Found"));
        return modelMappers.roleToDto(role);
    }

    @Override
    public RoleDto updateRole(RoleDto roleDto) {
        Role role = roleRepository.findById(roleDto.getId()).orElseThrow(() -> new RoleException("Role not found"));
        role.setName(roleDto.getName());
        return modelMappers.roleToDto(roleRepository.save(role));
    }

    @Override
    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }
}
