package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateMenuRequest(
        @NotBlank String label,
        String path,
        String parentCode,
        @NotNull Integer sortOrder,
        @NotBlank String menuType,
        String moduleKey,
        String description,
        List<String> permissionCodes
) {
}
