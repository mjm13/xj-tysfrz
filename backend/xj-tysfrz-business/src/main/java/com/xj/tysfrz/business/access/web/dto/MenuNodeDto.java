package com.xj.tysfrz.business.access.web.dto;

import java.util.List;

public record MenuNodeDto(
        String menuCode,
        String label,
        String path,
        String parentCode,
        Integer sortOrder,
        String menuType,
        String moduleKey,
        boolean visible,
        String description,
        List<String> permissionCodes,
        List<MenuNodeDto> children
) {
}
