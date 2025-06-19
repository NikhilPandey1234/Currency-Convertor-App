package com.login.service;

import com.login.dto.RoleDto;

import java.util.List;

public interface RoleService {

    RoleDto save(RoleDto roleDto);

    List<RoleDto> getAllRoles();

    RoleDto getRoleById(Long id);

    RoleDto updateRole(RoleDto roleDto);

    void deleteRoleById(Long id);
}
