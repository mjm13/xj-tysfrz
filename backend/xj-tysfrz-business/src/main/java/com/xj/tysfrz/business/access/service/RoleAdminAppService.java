package com.xj.tysfrz.business.access.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xj.tysfrz.business.access.infrastructure.PermissionEntity;
import com.xj.tysfrz.business.access.infrastructure.PermissionMapper;
import com.xj.tysfrz.business.access.infrastructure.RoleEntity;
import com.xj.tysfrz.business.access.infrastructure.RoleMapper;
import com.xj.tysfrz.business.access.web.dto.CreateRoleRequest;
import com.xj.tysfrz.business.access.web.dto.PermissionDto;
import com.xj.tysfrz.business.access.web.dto.ReplaceRolePermissionsRequest;
import com.xj.tysfrz.business.access.web.dto.RoleSummaryDto;
import com.xj.tysfrz.business.access.web.dto.UpdateRoleRequest;
import com.xj.tysfrz.common.exception.BizException;
import com.xj.tysfrz.common.response.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleAdminAppService {

    static final String SYSTEM_ROLE_ADMIN = "ADMIN";

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public RoleAdminAppService(RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    public List<RoleSummaryDto> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>().orderByAsc(RoleEntity::getRoleCode))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public List<PermissionDto> listPermissions() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                        .orderByAsc(PermissionEntity::getPermissionCode))
                .stream()
                .map(p -> new PermissionDto(p.getPermissionCode(), p.getModuleName(), p.getActionName()))
                .toList();
    }

    @Transactional
    public RoleSummaryDto createRole(CreateRoleRequest request) {
        if (SYSTEM_ROLE_ADMIN.equals(request.roleCode())) {
            throw new BizException(ErrorCode.FORBIDDEN.code(), "不可创建 ADMIN 系统角色");
        }
        if (roleMapper.selectCount(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, request.roleCode())) > 0) {
            throw new BizException("角色 code 已存在: " + request.roleCode());
        }
        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(request.roleCode());
        entity.setName(request.name());
        entity.setDescription(request.description());
        roleMapper.insert(entity);
        return toSummary(entity);
    }

    @Transactional
    public RoleSummaryDto updateRole(String roleCode, UpdateRoleRequest request) {
        RoleEntity entity = requireRole(roleCode);
        entity.setName(request.name());
        entity.setDescription(request.description());
        roleMapper.updateById(entity);
        return toSummary(entity);
    }

    @Transactional
    public RoleSummaryDto replaceRolePermissions(String roleCode, ReplaceRolePermissionsRequest request) {
        if (SYSTEM_ROLE_ADMIN.equals(roleCode)) {
            throw new BizException(ErrorCode.FORBIDDEN.code(), "不可修改 ADMIN 角色权限");
        }
        RoleEntity entity = requireRole(roleCode);
        Map<String, Long> permissionIds = loadPermissionIdMap();
        List<String> codes = request.permissionCodes() == null ? List.of() : request.permissionCodes();
        Set<String> unique = new HashSet<>(codes);
        for (String code : unique) {
            if (!permissionIds.containsKey(code)) {
                throw new BizException("未知 Permission: " + code);
            }
        }
        roleMapper.deleteRolePermissions(entity.getId());
        for (String code : unique) {
            roleMapper.insertRolePermission(entity.getId(), permissionIds.get(code));
        }
        return toSummary(entity);
    }

    private RoleEntity requireRole(String roleCode) {
        RoleEntity entity = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, roleCode));
        if (entity == null) {
            throw new BizException("角色不存在: " + roleCode);
        }
        return entity;
    }

    private Map<String, Long> loadPermissionIdMap() {
        return permissionMapper.selectList(null).stream()
                .collect(Collectors.toMap(PermissionEntity::getPermissionCode, PermissionEntity::getId));
    }

    private RoleSummaryDto toSummary(RoleEntity entity) {
        List<String> permissionCodes = roleMapper.findPermissionCodesByRoleId(entity.getId());
        boolean systemRole = SYSTEM_ROLE_ADMIN.equals(entity.getRoleCode());
        return new RoleSummaryDto(
                entity.getRoleCode(),
                entity.getName(),
                entity.getDescription(),
                permissionCodes,
                systemRole,
                !systemRole
        );
    }
}
