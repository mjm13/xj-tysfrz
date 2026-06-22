package com.xj.tysfrz.business.access.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xj.tysfrz.business.access.service.RoleAdminAppService;
import com.xj.tysfrz.business.access.web.dto.CreateRoleRequest;
import com.xj.tysfrz.business.access.web.dto.PermissionDto;
import com.xj.tysfrz.business.access.web.dto.ReplaceRolePermissionsRequest;
import com.xj.tysfrz.business.access.web.dto.RoleSummaryDto;
import com.xj.tysfrz.business.access.web.dto.UpdateRoleRequest;
import com.xj.tysfrz.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class RoleAdminController {

    private final RoleAdminAppService roleAdminAppService;

    public RoleAdminController(RoleAdminAppService roleAdminAppService) {
        this.roleAdminAppService = roleAdminAppService;
    }

    @GetMapping("/roles")
    @SaCheckPermission("admin:roles:read")
    public ApiResponse<List<RoleSummaryDto>> listRoles() {
        return ApiResponse.ok(roleAdminAppService.listRoles());
    }

    @PostMapping("/roles")
    @SaCheckPermission("admin:roles:write")
    public ApiResponse<RoleSummaryDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.ok(roleAdminAppService.createRole(request));
    }

    @PutMapping("/roles/{roleCode}")
    @SaCheckPermission("admin:roles:write")
    public ApiResponse<RoleSummaryDto> updateRole(
            @PathVariable String roleCode,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ApiResponse.ok(roleAdminAppService.updateRole(roleCode, request));
    }

    @PutMapping("/roles/{roleCode}/permissions")
    @SaCheckPermission("admin:roles:write")
    public ApiResponse<RoleSummaryDto> replaceRolePermissions(
            @PathVariable String roleCode,
            @Valid @RequestBody ReplaceRolePermissionsRequest request
    ) {
        return ApiResponse.ok(roleAdminAppService.replaceRolePermissions(roleCode, request));
    }

    @GetMapping("/permissions")
    @SaCheckPermission("admin:roles:read")
    public ApiResponse<List<PermissionDto>> listPermissions() {
        return ApiResponse.ok(roleAdminAppService.listPermissions());
    }
}
