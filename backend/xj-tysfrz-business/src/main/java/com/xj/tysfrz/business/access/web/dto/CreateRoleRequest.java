package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateRoleRequest(
        @NotBlank
        @Pattern(regexp = "[A-Z][A-Z0-9_]{1,62}", message = "roleCode 须为大写字母、数字或下划线")
        String roleCode,
        @NotBlank String name,
        String description
) {
}
