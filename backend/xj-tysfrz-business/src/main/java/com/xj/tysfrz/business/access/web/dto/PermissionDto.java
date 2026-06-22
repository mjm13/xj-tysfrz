package com.xj.tysfrz.business.access.web.dto;

public record PermissionDto(
        String permissionCode,
        String moduleName,
        String actionName
) {
}
