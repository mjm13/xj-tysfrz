package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateMenuRequest(
        @NotBlank String menuCode,
        @NotBlank String label,
        String path,
        String parentCode,
        @NotNull Integer sortOrder,
        @NotBlank String menuType,
        String moduleKey,
        String description,
        @NotNull List<String> permissionCodes
) {
}
